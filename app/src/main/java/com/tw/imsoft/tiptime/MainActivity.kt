package com.tw.imsoft.tiptime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tw.imsoft.tiptime.ui.theme.TipTimeTheme
import java.text.NumberFormat
import androidx.compose.material.SwitchDefaults
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipTimeTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    TipTimeScreen()
                }
            }
        }
    }
}
@Composable
fun TipTimeScreen() {
    var roundUp by remember { mutableStateOf(false) }
    var amountInput by remember { mutableStateOf("") }
    var tipInput by remember { mutableStateOf("") }
    val amount = amountInput.toDoubleOrNull() ?: 0.0
    val tipPercent = tipInput.toDoubleOrNull() ?: 0.0
//    val tip = calculateTip(amount)
    val tip = calculateTip(amount, roundUp, tipPercent)

    //將使用 KeyboardActions 類別實作鍵盤功能
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = stringResource(R.string.calculate_tip),
            fontSize = 24.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(16.dp))
        EditNumberField(
            label = R.string.bill_amount,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                //要求 FocusManager 將焦點向下移動至下一個可組合項「Tip %」。接著在 lambda 運算式中，
                // 呼叫 focusManager 物件的 moveFocus() 函式並傳入 FocusDirection.Down 引數
                onNext =  { focusManager.moveFocus(FocusDirection.Down) }
            ),
            value = amountInput,
            onValueChange = { amountInput = it }
        )
        EditNumberField(
            label = R.string.how_was_the_service,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                //要求 FocusManager 將焦點向下移動至下一個可組合項「Tip %」。接著在 lambda 運算式中，
                // 呼叫 focusManager 物件的 moveFocus() 函式並傳入 FocusDirection.Down 引數
                onDone = { focusManager.clearFocus() }
            ),
            value = tipInput,
            onValueChange = { tipInput = it}
        )

        RoundTheTipRow(roundUp = roundUp, onRoundUpChanged = { roundUp = it })
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.tip_amount, tip),
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = stringResource(id = label),
                modifier = Modifier.fillMaxWidth()

            )
        },
        singleLine = true,
        keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions

    )
}

//switch 可組合項
@Composable
fun RoundTheTipRow(
    modifier: Modifier = Modifier,
    roundUp: Boolean,
    onRoundUpChanged: (Boolean) -> Unit,) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .size(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = stringResource(R.string.round_up_tip))
        Switch(
            checked = roundUp, //是否已勾選切換按鈕。這是 Switch 可組合項的狀態。
            onCheckedChange = onRoundUpChanged, //使用者點選切換按鈕時要呼叫的回呼。
            modifier = modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = Color.DarkGray
            )
        )
    }
}



private fun calculateTip(amount: Double, roundUp: Boolean,
                         tipPercent: Double = 15.0): String {
    var tip = tipPercent / 100 * amount

    if (roundUp)
        tip = kotlin.math.ceil(tip)

    return NumberFormat.getCurrencyInstance().format(tip)
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    TipTimeTheme {
        TipTimeScreen()
    }
}