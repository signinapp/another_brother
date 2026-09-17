//
//  GetPrinterStatusMethodCall.m
//  another_brother
//
//  Created by admin on 4/15/21.
//

#import <Foundation/Foundation.h>
#import "GetPrinterStatusMethodCall.h"

@implementation GetPrinterStatusMethodCall
static NSString * METHOD_NAME = @"getPrinterStatus";

- (instancetype)initWithCall:(FlutterMethodCall *)call
                      result:(FlutterResult) result {
    self = [super init];
        if (self) {
            _call = call;
            _result = result;
            
        }
        return self;
}

+ (NSString *) METHOD_NAME {
    return METHOD_NAME;
}
- (void)execute {
    dispatch_queue_t queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0ul);
    dispatch_async(queue, ^{
        // Get printInfo dart params from call
        NSDictionary<NSString *, NSObject *> * dartPrintInfo = self->_call.arguments[@"printInfo"];

        // TODO Get channel from printInfo
        BRLMChannel *channel = [BrotherUtils printChannelWithPrintSettingsMap:dartPrintInfo];

        // TODO Generate printer driver
        BRLMPrinterDriverGenerateResult * driverGenerateResult = [BRLMPrinterDriverGenerator openChannel:channel];
        if (driverGenerateResult.error.code != BRLMOpenChannelErrorCodeNoError ||
            driverGenerateResult.driver == nil) {

            // On Error report error
            NSDictionary<NSString *, NSObject *> * printStatus = [BrotherUtils printerStatusToMapWithError:BRLMPrintErrorCodePrinterStatusErrorCommunicationError  status:nil];
            dispatch_sync(dispatch_get_main_queue(), ^{
                self->_result(printStatus);
            });
            return;
        }

        BRLMPrinterDriver *printerDriver = driverGenerateResult.driver;

        // Call print method
        BRLMGetPrinterStatusResult * status = [printerDriver getPrinterStatus];

        [printerDriver closeChannel];

        BRLMPrintErrorCode errorCode = [BrotherUtils statusErrorCodeToMapWithRawStatus:status.status.ptStatus];

        // Notify status to Flutter.
        NSDictionary<NSString *, NSObject *> * printStatus = [BrotherUtils printerStatusToMapWithError:errorCode status:status.status];

        dispatch_sync(dispatch_get_main_queue(), ^{
            self->_result(printStatus);
        });
    });
}


@end
