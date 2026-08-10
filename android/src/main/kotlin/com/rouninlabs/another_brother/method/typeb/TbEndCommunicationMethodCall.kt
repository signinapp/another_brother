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
 * Command for ending a communication to a printer.
 */
class TbEndCommunicationMethodCall(val flutterAssets: FlutterPlugin.FlutterAssets, val context: Context, val call: MethodCall, val result: MethodChannel.Result) {
    companion object {
        const val METHOD_NAME = "typeB-endCommunication"
    }

    fun execute() {

        GlobalScope.launch(Dispatchers.IO) {
            // Held outside the try so a failure can still report the port as open.
            var openPrinterId: String? = null
            try {

                val printerId: String = call.argument<String>("printerId")!!
                openPrinterId = printerId
                val dartPrintInfo: HashMap<String, Any> = call.argument<HashMap<String, Any>>("printInfo")!!
                val timeout:Int = call.argument<Int>("timeout")!!

                val tbPrinter:ITbPrinterAdapter? = BrotherManager.getTypeBPrinter(printerId = printerId)
                if (tbPrinter == null) {
                    withContext(Dispatchers.Main) {
                        // Set result Printer status.
                        result.success(printerId)
                    }
                    return@launch
                }
                // Close connection
                val success:Boolean = tbPrinter.closePort(timeout)

                if (!success) {
                    withContext(Dispatchers.Main) {
                        // Set result Printer status.
                        result.success(printerId)
                    }
                    return@launch
                }

                BrotherManager.untrackTypeBPrinter(printerId = tbPrinter.getId())
                // On Success track printer
                withContext(Dispatchers.Main) {
                   // Set result Printer status.
                   result.success("")
               }
            } catch (t: Throwable) {
                Log.e("another-brother", "typeB-endCommunication error: ", t);
                withContext(Dispatchers.Main) {
                    // Echoing the printer id reports the port as still open, matching the
                    // handler's other failure paths. An empty string would claim it closed.
                    val printerId = openPrinterId
                    if (printerId != null) {
                        result.success(printerId)
                    } else {
                        result.error(METHOD_NAME, t.message, null)
                    }
                }
            }
        }

    }
}