package com.example.calculatorjc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculatorjc.ui.theme.black
import com.example.calculatorjc.ui.theme.yellow
import java.lang.Exception
import java.text.DecimalFormat


class FragmentTwo : Fragment() {


    private var btnText by mutableStateOf("")
    private var inputOne by mutableStateOf("")
    private var inputTwo by mutableStateOf("")

    private var animationVisibility by mutableStateOf(false)

    companion object {
        const val inputOneKey = "InputOne"
        const val inputTwoKey = "InputTwo"
        const val buttonText = "ButtonText"
    }

    private lateinit var callBack: Result
    interface Result {
        fun sendResult(result: String)
    }

    fun setOnResultSender(callBack: Result) {
        this.callBack = callBack
    }

    fun updateActionBtnText(text: String) {
        btnText = text
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        arguments?.getString(inputOneKey)?.let {
            inputOne = it
        }
        arguments?.getString(inputTwoKey)?.let {
            inputTwo = it
        }

        val view = ComposeView(requireContext())

        return view.apply {

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            setContent {
                val isTab: Boolean = ResourcesClass.isTablet(context = context)
                InflateContent(isTab)
            }
        }
    }

    @Composable
    fun InflateContent(isTab: Boolean) {

        val isDarkTheme = isSystemInDarkTheme()
        val backGround = if (isDarkTheme) black else yellow

        val focusManager = LocalFocusManager.current

        LaunchedEffect( key1 = Unit, block = {
            animationVisibility = true
        } )

        AnimatedVisibility(visible = animationVisibility,
            enter =  slideInHorizontally(
            tween(
            durationMillis = 400),
            initialOffsetX = {-it}),
            exit =  slideOutHorizontally(
                tween(
                    durationMillis = 400),
                targetOffsetX = {-it})

        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backGround),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                val textFieldModifier = if (!isTab) Modifier
                    .padding(bottom = 30.dp)
                else
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 120.dp)
                        .padding(bottom = 30.dp)


                val btnModifier = if(!isTab) Modifier

                else {
                    Modifier
                        .padding(top = 30.dp)
                        .wrapContentWidth()
                        .clip(CutCornerShape(10.dp))
                }

                InputOne(
                    textFieldModifier, isTab
                )

                InputTwo(modifier = textFieldModifier, focusManager, isTab)


                ActionButton(
                    btnModifier, focusManager, isTab
                ) { animationVisibility = false }

            }
        }
    }

    @Composable
    private fun InputOne(modifier: Modifier, isTab: Boolean) {

        val labelSize = if(!isTab) 15.sp else 18.sp
        val regex = Regex("\\d*\\.?\\d*")
        TextField (
            value = inputOne, onValueChange = {
                if (regex.matches(it)) {
                    inputOne = it
                } else {
                    Toast.makeText(context,"${it[it.length - 1]} is not valid", Toast.LENGTH_SHORT).show()
                }
            },

            modifier = modifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Next
            ),
            singleLine = true,

            label = { Text(ResourcesClass.numberOne,  color = MaterialTheme.colors.onPrimary, fontSize = labelSize) },
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onPrimary),
            textStyle = MaterialTheme.typography.h5

        )
    }

    @Composable
    private fun InputTwo(
        modifier: Modifier,
        focusManager: FocusManager,
        isTab: Boolean
    ) {

        val labelSize = if(!isTab) 15.sp else 18.sp

        val regex = Regex("\\d*\\.?\\d*")

        TextField (
            value = inputTwo,
            onValueChange = {
                if (regex.matches(it)) {
                    inputTwo = it
                } else {
                    Toast.makeText(context,"${it[it.length - 1]} is not valid", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = modifier,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number, imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { focusManager.clearFocus() }),
            singleLine = true,
            label = { Text(ResourcesClass.numberTwo, color = MaterialTheme.colors.onPrimary, fontSize = labelSize)},
            colors = TextFieldDefaults.textFieldColors(textColor = MaterialTheme.colors.onPrimary),
            textStyle = MaterialTheme.typography.h5
        )
    }

    @Composable
    private fun ActionButton(
        modifier: Modifier,
        focusManager: FocusManager,
        isTab: Boolean,
        function: () -> Unit
    ) {

        Button (onClick = {
            focusManager.clearFocus()
            onClick(function)
        }, modifier = modifier) {
            if (arguments?.getString(buttonText) != null) {
                btnText = arguments?.getString(buttonText)!!
            }
            val fontSize = if(!isTab) 15.sp else 25.sp
            Text(text = btnText, fontSize = fontSize)
        }
    }

    private fun onClick(function: () -> Unit) {

        if (inputTwo == "0" && btnText == Actions.Division.name) {
            Toast.makeText(context, resources.getString(R.string.dividedByZero), Toast.LENGTH_SHORT).show()
        } else if (inputOne.isNotEmpty() && inputTwo.isNotEmpty() && inputOne != "." && inputTwo != ".") {

            function()

            generateResult(
                inputOne,
                inputTwo,
                Actions.valueOf(btnText)
            )
            resetInputs()

        } else {
            Toast.makeText(context,resources.getString(R.string.wrongInput), Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateResult(input1: String, input2: String, action: Actions) {

        try {
            val num1 = input1.toFloat()
            val num2 = input2.toFloat()

            val ans = when (action) {
                Actions.Add -> (num1 + num2)
                Actions.Subtract -> (num1 - num2)
                Actions.Multiply -> (num1 * num2)
                Actions.Division -> (num1 / num2)
            }

            val format = DecimalFormat("0.#")
            val resultText = resources.getString (R.string.result, format.format(ans).toString(), input1, input2, action)

            callBack.sendResult(resultText)
        }
        catch (e: Exception) {
            Toast.makeText(context, resources.getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
    }


    override fun onPause() {

        super.onPause()
        arguments?.putString(inputOneKey, inputOne)
        arguments?.putString(inputTwoKey, inputTwo)
    }

    fun resetInputs() {
        inputTwo = ""
        inputOne = ""
        arguments = null
    }
}

