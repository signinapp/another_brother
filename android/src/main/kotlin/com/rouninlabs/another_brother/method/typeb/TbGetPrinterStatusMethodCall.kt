package com.rouninlabs.another_brother.method.typeb

import android.content.Context
import android.util.Log
import com.brother.ptouch.sdk.Printer
import com.brother.ptouch.sdk.PrinterInfo
import com.brother.ptouch.sdk.PrinterStatus
import com.rouninlabs.another_brother.BrotherManager
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.*

/**
 * Command for getting the printer status.
 */
class TbGetPrinterStatusMethodCall(val flutterAssets: FlutterPlugin.FlutterAssets, val context: Context, val call: MethodCall, val result: MethodChannel.Result) {
    companion object {
        const val METHOD_NAME = "typeB-printerStatus"
    }

    fun execute() {

        GlobalScope.launch(Dispatchers.IO) {
            try {

                val dartPrintInfo: HashMap<String, Any> = call.argument<HashMap<String, Any>>("printInfo")!!
                val printerId: String = call.argument<String>("printerId")!!
                val delayMillis = call.argument<Int>("delayMillis")!!

            
                val tbPrinter:ITbPrinterAdapter? = BrotherManager.getTypeBPrinter(printerId = printerId)

                if (tbPrinter == null) {
                    withContext(Dispatchers.Main) {
                        // Set result Printer status.
                        result.success(hashMapOf(
                                "value" to "80"
                        ))
                    }
                    return@launch
                }

                val printStatus:String = tbPrinter.printerStatus(delay = delayMillis);
            
                if (printStatus.isEmpty() || printStatus == "-1") {
                    withContext(Dispatchers.Main) {
                        // Set result Printer status.
                        result.success(hashMapOf(
                                "value" to "80"
                        ))
                    }
                    return@launch
                }

                // On Success track printer
                withContext(Dispatchers.Main) {
                   // Set result Printer status.
                   result.success(hashMapOf(
                           "value" to printStatus
                   ))
               }
            } catch (t: Throwable) {
                Log.e("another-brother", "typeB-printerStatus error: ", t);
                withContext(Dispatchers.Main) {
                    result.success(hashMapOf(
                            "value" to "80"
                    ))
                }
            }
        }

    }
}