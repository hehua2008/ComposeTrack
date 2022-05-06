package com.composetrack.demo

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hym.composetrack.*

class MainActivity : ComponentActivity() {
    private val TAG = this.toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // 获取根节点
            currentRootNode.apply {
                modifier = modifier.identity("root") // 为根节点设置 root 标识
            }

            SetClickableCallback(object : ClickableCallback {
                override fun onClick(layoutNodeInfo: LayoutNodeInfo) {
                    Log.w(TAG, "onClick: ${layoutNodeInfo.identityPath}")
                }

                override fun onLongClick(layoutNodeInfo: LayoutNodeInfo) {
                    Log.w(TAG, "onLongClick: ${layoutNodeInfo.identityPath}")
                }

                override fun onDoubleClick(layoutNodeInfo: LayoutNodeInfo) {
                    Log.w(TAG, "onDoubleClick: ${layoutNodeInfo.identityPath}")
                }
            })

            // 设置重组状态收集
            CollectRecomposeState {
                if (it == RecomposeState.Start) {
                    Log.w(TAG, "state recompose start")
                } else if (it == RecomposeState.End) {
                    Log.w(TAG, "state recompose end")
                }
            }

            TraceListener.registerTraceCallback(
                TraceListener.RECOMPOSER_RECOMPOSE,
                object : TraceListener.TraceCallback {
                    override fun onTraceBegin(name: String) {
                        Log.w(TAG, "callback recompose start")
                    }

                    override fun onTraceEnd(name: String) {
                        Log.w(TAG, "callback recompose end")
                    }
                }
            )

            // 为此节点设置 Column 标识
            Column(modifier = Modifier.identity("Column")) {
                Greeting("Android")
                // 为此节点设置 Row 标识
                Row(modifier = Modifier.identity("Row")) {
                    Greeting("iOS")
                }
            }
        }
    }

    @Composable
    fun Greeting(name: String) {
        var showText by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier.identity("Greeting-$name"),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 保存标识引用，以便在 onClick 块中获取此 Button 对应的 LayoutNode 节点
            val buttonId = rememberIdentity("ButtonId")
            Button(
                modifier = Modifier.identity(buttonId), // 为此节点设置 ButtonId 标识
                onClick = {
                    // 通过 buttonId 获取此节点的路径
                    Log.w(TAG, "onClick: ${buttonId.path}")
                }
            ) {
                // 保存标识引用，以便在 clickable 块中获取此 Text 对应的 LayoutNode 节点
                val textId = rememberIdentity("TextId")
                Text(
                    text = "Hello $name!",
                    modifier = Modifier
                        .identity(textId) // 为此节点设置 TextId 标识
                        .clickable {
                            showText = !showText
                            // 通过 textId 获取此节点的路径
                            Log.w(TAG, "onClick: ${textId.path}")
                        }
                )
            }
            if (showText) {
                val showTextId = rememberIdentity("ShowTextId", object : OnAttachOnDetachCallback {
                    override fun onAttach(nodeIdentity: NodeIdentity) {
                        Log.w(TAG, "onAttach: ${nodeIdentity.path}")
                    }

                    override fun onDetach(nodeIdentity: NodeIdentity) {
                        Log.w(TAG, "onDetach: ${nodeIdentity.name}")
                    }
                })
                Text(text = "show $name", modifier = Modifier.identity(showTextId))
            }
        }
    }

    @Composable
    fun MyText(text: String, onShow: () -> Unit, onDispose: () -> Unit) {
        var showing by remember { mutableStateOf(false) }
        if (!showing) {
            showing = true
            onShow()
        }

        Text(text = text)

        DisposableEffect(Unit) {
            onDispose {
                showing = false
                onDispose()
            }
        }
    }
}
