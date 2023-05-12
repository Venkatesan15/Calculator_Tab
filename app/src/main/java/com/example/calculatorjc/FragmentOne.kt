package com.example.calculatorjc


import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import com.example.calculatorjc.ui.theme.black
import com.example.calculatorjc.ui.theme.pink


class FragmentOne : Fragment() {

    private var resultText = ""

    private val buttonItem = 1
    private val resultItem = 2

    private lateinit var addObj: ActionOrResItem
    private lateinit var subObj: ActionOrResItem
    private lateinit var mulObj: ActionOrResItem
    private lateinit var divObj: ActionOrResItem

    private var isBtnDisabled by mutableStateOf(false)

    private lateinit var reset: String

    private val actionOrResItems = mutableStateListOf<ActionOrResItem>()

    private lateinit var callBack: Action


    companion object {

        const val resultAvailable = "ResultAvailable"
    }


    interface Action {
        fun sendActionText (text: String): Boolean
    }

    fun setOnActionSender(callBack: Action) {
        this.callBack = callBack
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        initialize()

        return ComposeView(requireContext()).apply {

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {

                val isTab: Boolean = isTab()
                InflateActions(actionOrResItems, isTab)
            }

        }
    }

    @Composable
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    private fun isTab(): Boolean {
        val windowSizeClass = calculateWindowSizeClass(requireActivity())
        val height = windowSizeClass.heightSizeClass

        val isTab: Boolean =
            if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                height == WindowHeightSizeClass.Expanded
            } else {
                height != WindowHeightSizeClass.Compact
            }
        return isTab
    }

    private fun initialize() {

        addObj = ActionOrResItem(buttonItem, ResourcesClass.addBtn)
        subObj = ActionOrResItem(buttonItem, ResourcesClass.subtractBtn)
        mulObj = ActionOrResItem(buttonItem, ResourcesClass.multiply)
        divObj = ActionOrResItem(buttonItem, ResourcesClass.division)

        reset = ResourcesClass.reset

        //This function will add result(if result available) and reset button into the adapter when orientation change
        addResult()

        if (actionOrResItems.size == 0) addActionsIntoAdapter()

    }

    private fun addResult() {

        if (arguments?.getString(resultAvailable) != null) {

            actionOrResItems.add(
                ActionOrResItem(
                    resultItem,
                    arguments?.getString(resultAvailable)!!
                )
            )
            actionOrResItems.add(
                ActionOrResItem(
                    buttonItem,
                    ResourcesClass.reset
                )
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun InflateActions(
        actionOrResItems: SnapshotStateList<ActionOrResItem>,
        isTab: Boolean
    ) {


        val isDarkTheme = isSystemInDarkTheme()
        val backGround = if(isDarkTheme) black else pink

        var visible by remember {
            mutableStateOf(false)
        }
        LaunchedEffect (key1 = Unit, block = {
            visible = true
        })

        AnimatedVisibility(visible = visible,
        enter = slideInVertically(
            tween(
                durationMillis = 400,
            ),initialOffsetY = {it},

        )
        ) {

            LazyColumn(
                modifier = Modifier.background(backGround).fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                items(actionOrResItems.size, key = {it}) { index ->

                    if (actionOrResItems[index].itemType == buttonItem ) {

                        val mod: Modifier =
                        if(!isTab)
                            Modifier.width(150.dp).animateItemPlacement()
                        else Modifier.width(300.dp).padding( bottom = 30.dp).clip(
                            CutCornerShape(topStart = 40.dp, bottomEnd = 40.dp)
                        ).animateItemPlacement()

                        ButtonItem(index = index, mod, isTab)
                    } else {

                        Text(
                            text = actionOrResItems[index].text,
                            fontSize = 30.sp,
                            modifier = Modifier.padding(20.dp).padding(bottom = 30.dp).animateItemPlacement().fillMaxWidth(),
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                }
            }
        }
    }


    @Composable
    private fun ButtonItem (
        index: Int,
        modifier: Modifier,
        isTab: Boolean
    ) {

        var onClick by remember {
            mutableStateOf(false)
        }
        var actionText by remember {
            mutableStateOf("")
        }

        if (onClick) onClick = callBack.sendActionText(actionText)

        Button(modifier = modifier,
            onClick = {
                    actionText = actionOrResItems[index].text
                    if (actionText == reset) {
                        (activity as MainActivity).isNavBtnVisible = false
                        resultText = ""
                        arguments = null
                        actionOrResItems.clear()
                        addActionsIntoAdapter()
                    } else {
                        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) isBtnDisabled = true
                        onClick = true
                    }

            },
        enabled = !isBtnDisabled)
        {
            val fontSize = if(!isTab) 15.sp else 20.sp
            Text(text = actionOrResItems[index].text, fontSize = fontSize)
        }
    }

     fun addActionsIntoAdapter() {

        actionOrResItems.clear()
        actionOrResItems.add(addObj)
        actionOrResItems.add(subObj)
        actionOrResItems.add(mulObj)
        actionOrResItems.add(divObj)
    }
    override fun onSaveInstanceState(outState: Bundle) {

        if (resultText.isNotEmpty()) {
            arguments?.putString(resultAvailable, resultText)
        }
        super.onSaveInstanceState(outState)
    }

    fun addResultIntoAdapter(result: String) {

        resultText = result
        actionOrResItems.clear()
        actionOrResItems.add(ActionOrResItem(resultItem, result))
        actionOrResItems.add(ActionOrResItem(buttonItem, ResourcesClass.reset))
    }

    fun enableButtons() {
        isBtnDisabled = false
    }

}