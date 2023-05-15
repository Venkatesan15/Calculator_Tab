package com.example.calculatorjc

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentOnAttachListener
import com.example.calculatorjc.ui.theme.CalculatorJCTheme
import com.example.calculatorjc.ui.theme.black
import com.example.calculatorjc.ui.theme.white


class MainActivity : AppCompatActivity(), FragmentOne.Action,
    FragmentTwo.Result {

    private lateinit var fragmentTwo: FragmentTwo
    private lateinit var fragmentOne: FragmentOne
    private lateinit var actionFrgContainer: FrameLayout

    private var containerOneId = 0
    private var containerTwoId = 1

    var isTopAppBarVisible by mutableStateOf(false)

    companion object {
        const val fragmentOneTag = "FragmentOne"
        const val frgBTag = "FragmentB"
        const val fragTwoArg = "fragTwoArg"
        const val fragmentOneArg = "FragmentOneArg"
        const val isBackBtnVisibleTag ="IsBackBtnVisible"

        const val isFrgBVisibleNow = "IsFrgBVisibleNow"
    }


    private var isFrgBVisible by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {

        val listener = FragmentOnAttachListener { _, fragment ->
            onAttachFragments(fragment = fragment)
        }
        supportFragmentManager.addFragmentOnAttachListener(listener)


        if(savedInstanceState != null && savedInstanceState.getBoolean(isBackBtnVisibleTag)) isTopAppBarVisible = true

        setContent {

            InitializeContainers(savedInstanceState)

            CalculatorJCTheme {
                Scaffold(
                    topBar = { if (isTopAppBarVisible) AddTopAppBar() },
                    content = { paddingValues -> InflateFragments(paddingValues) }
                )
            }

        }
        super.onCreate(savedInstanceState)
    }

    @Composable
    private fun InflateFragments(paddingValues: PaddingValues) {

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            LoadActionFragment(Modifier, isFrgBVisible)

            if (isFrgBVisible) {
                LoadInputFragment()
            }
        } else {

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                LoadActionFragment(
                    modifier = Modifier
                        .fillMaxWidth(0.5f),
                    isFrgBVisible
                )

                Divider(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(5.dp), color = MaterialTheme.colors.onPrimary
                )


                if (isFrgBVisible) {
                    LoadInputFragment(
                        modifier = Modifier
                            .weight(0.5f)
                            .width(0.dp)
                    )
                }
            }
        }
    }
    @Composable
    private fun InitializeContainers(savedInstanceState: Bundle?) {

        val containerOne by rememberSaveable {
            mutableStateOf(ViewCompat.generateViewId())
        }
        val containerTwo by rememberSaveable {
            mutableStateOf(ViewCompat.generateViewId())
        }

        this.containerOneId = containerOne
        this.containerTwoId = containerTwo

        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)
        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)

        if (savedInstanceState?.getBundle(fragTwoArg) != null) {
            frgB?.arguments = savedInstanceState.getBundle(fragTwoArg)
        }
        if (savedInstanceState?.getBundle(fragmentOneArg) != null) {
            frgA?.arguments = savedInstanceState.getBundle(fragmentOneArg)
        }

        if(savedInstanceState != null && savedInstanceState.getBoolean(isFrgBVisibleNow)) {
            isFrgBVisible = savedInstanceState.getBoolean(isFrgBVisibleNow)
        }
    }

    @Composable
    private fun LoadActionFragment(modifier: Modifier = Modifier, isFragmentBVisible: Boolean) {


        AndroidView(
            factory = {
                FrameLayout(it).apply {

                    fragmentOne = FragmentOne()
                    id = containerOneId
                    actionFrgContainer = this

                    val frg: Fragment? = supportFragmentManager.findFragmentByTag(fragmentOneTag)

                    if (frg != null)
                        fragmentOne.arguments = frg.arguments

                    supportFragmentManager.beginTransaction()
                        .replace(containerOneId, fragmentOne, fragmentOneTag)
                        .commit()

                }

            },
            modifier = modifier,
            update = {
                if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT && isFragmentBVisible) {

                    it.visibility = View.GONE
                }
            }
        )
    }

    @Composable
    private fun LoadInputFragment(modifier: Modifier = Modifier) {

        AndroidView(
            factory = {

                FrameLayout(it).apply {
                    id = containerTwoId

                    val fragmentTwoNew = FragmentTwo()


                    val frgB = supportFragmentManager.findFragmentByTag(frgBTag)

                    if (frgB?.arguments != null)
                        fragmentTwoNew.arguments = frgB.arguments
                    else fragmentTwoNew.arguments = fragmentTwo.arguments

                    supportFragmentManager.beginTransaction()
                        .replace(containerTwoId, fragmentTwoNew, frgBTag)
                        .commit()
                }
            }, modifier = modifier
        )

    }


    override fun onSaveInstanceState(outState: Bundle) {

        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)
        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)

        if (frgA != null ) outState.putBundle(fragmentOneArg, frgA.arguments)
        if (frgB != null) {
            outState.putBundle(fragTwoArg, frgB.arguments)}

        outState.putBoolean(isBackBtnVisibleTag, isTopAppBarVisible)
        outState.putBoolean(isFrgBVisibleNow, isFrgBVisible)
        super.onSaveInstanceState(outState)
    }


    //This function is used for send the Action(Add, Subtract,..) from FragmentOne to FragmentTwo
    override fun sendActionText(text: String): Boolean {

        isTopAppBarVisible = true
        isFrgBVisible = true

        val bundle = Bundle()

        bundle.putString(FragmentTwo.buttonText, text)

        if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)
            if (frgA != null) {
                actionFrgContainer.visibility = View.GONE
            }
        }

        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)

        if (frgB != null) {
            frgB.arguments = bundle
            (frgB as FragmentTwo).updateActionBtnText(text)

        } else {
            fragmentTwo = FragmentTwo()
            fragmentTwo.arguments = bundle
            fragmentTwo.updateActionBtnText(text)
        }


        return false
    }

    //This function is used for get Result from FragmentTwo and send it to FragmentOne
    override fun sendResult(result: String) {

        val bundle = Bundle()
        bundle.putString(FragmentOne.resultAvailable, result)

        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)
        if (frgA != null) {
            if(resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                actionFrgContainer.visibility = View.VISIBLE
            }
            frgA.arguments = bundle
            (frgA as FragmentOne).addResultIntoAdapter(result)
            frgA.enableButtons()

        }

        isFrgBVisible = false

    }

    private fun onAttachFragments(fragment: Fragment) {
        if (fragment is FragmentOne) {
            fragment.setOnActionSender(this)
        } else if (fragment is FragmentTwo) {
            fragment.setOnResultSender(this)
        }
    }



    @Composable
    fun AddTopAppBar() {


        val isTab: Boolean = ResourcesClass.isTablet(context = this)

        val modifier = if(isTab) Modifier
            .fillMaxWidth()
            .height(100.dp) else Modifier

        val tintColor = if(isSystemInDarkTheme()) white else black
        TopAppBar(
            title = {
                    Text(text = ResourcesClass.calculator, modifier = Modifier.fillMaxWidth())
         },
            modifier = modifier,

            navigationIcon = {
                IconButton(onClick = {
                    onBackClick()
                }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            contentColor = MaterialTheme.colors.onPrimary,

            backgroundColor = MaterialTheme.colors.secondaryVariant,
            elevation = 0.dp,
            actions = {
                Icon(painter = painterResource(id = R.drawable.ic_launcher_foreground), contentDescription = "Logo", tint = tintColor, modifier = Modifier.clickable {
                    Toast.makeText(this@MainActivity, resources.getString(R.string.actionIconToast), Toast.LENGTH_SHORT).show()
                })
            }
        )
    }




    private fun onBackClick() {

        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager

            if (imm != null) {
                if(imm.hideSoftInputFromWindow(view.windowToken, 0)) return
            }
        }
        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)


        if (isFrgBVisible) {
            isTopAppBarVisible = false

            if(frgA != null) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    actionFrgContainer.visibility = View.VISIBLE
                    (frgA as FragmentOne).enableButtons()
                }
            }

            val frgB = supportFragmentManager.findFragmentByTag(frgBTag)
            (frgB as FragmentTwo).resetInputs()
            isFrgBVisible = false

        } else  {

            if (frgA != null) {
                isTopAppBarVisible = false
                (frgA as FragmentOne).addActionsIntoAdapter()
                frgA.arguments = null
            }
        }
    }


    override fun onBackPressed() {

        val frgB = supportFragmentManager.findFragmentByTag(frgBTag)
        val frgA = supportFragmentManager.findFragmentByTag(fragmentOneTag)

        if (frgA?.arguments == null) isTopAppBarVisible = false

        if (isFrgBVisible) {

            if(frgA != null) {
                if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                    actionFrgContainer.visibility = View.VISIBLE
                    (frgA as FragmentOne).enableButtons()
                }
            }
            (frgB as FragmentTwo).resetInputs()
            isFrgBVisible = false

        } else super.onBackPressed()
    }
}