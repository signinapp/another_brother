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
 * Command for printing a label.
 */
class TbPrintLabelMethodCall(val flutterAssets: FlutterPlugin.FlutterAssets, val context: Context, val call: MethodCall, val result: MethodChannel.Result) {
    companion object {
        const val METHOD_NAME = "typeB-printLabel"
    }

    fun execute() {

        GlobalScope.launch(Dispatchers.IO) {
            try {

                val dartPrintInfo: HashMap<String, Any> = call.argument<HashMap<String, Any>>("printInfo")!!
                val printerId: String = call.argument<String>("printerId")!!
                val quantity:Int = call.argument<Int>("quantity")!!
                val copy:Int = call.argument<Int>("copy")!!

                val tbPrinter:ITbPrinterAdapter? = BrotherManager.getTypeBPrinter(printerId = printerId)

                if (tbPrinter == null) {
                    withContext(Dispatchers.Main) {
                        // Set result Printer status.
                        result.success(false)
                    }
                    return@launch
                }
                // Setup the label
                val success = tbPrinter.printLabel(quantity = quantity, copy = copy)
            
                if (!success) {
                    withContext(Dispatchers.Main) {
                        // Set result Printer status.
                        result.success(false)
                    }
                    return@launch
                }

                // On Success track printer
                withContext(Dispatchers.Main) {
                   // Set result Printer status.
                   result.success(true)
               }
            } catch (t: Throwable) {
                Log.e("another-brother", "typeB-printLabel error: ", t);
                withContext(Dispatchers.Main) {
                    result.success(false)
                }
            }
        }

    }
}