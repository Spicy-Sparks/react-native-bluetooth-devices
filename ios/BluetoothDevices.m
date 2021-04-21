#import "BluetoothDevices.h"
#import <AVFoundation/AVFoundation.h>
#import <AudioToolbox/AudioToolbox.h>

@implementation BluetoothDevices
@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE();

RCT_EXPORT_METHOD(startScan) {
    [[NSNotificationCenter defaultCenter]
    addObserver:self
    selector: @selector(routeChanged:)
    name:AVAudioSessionRouteChangeNotification
    object:[AVAudioSession sharedInstance]];

    dispatch_async(dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0), ^{
        [self sendInformations];
    });
}

RCT_EXPORT_METHOD(disconnectFromDevice:(NSString *) deviceId) {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

RCT_EXPORT_METHOD(connectToDevice:(NSString *) deviceId) {
}

- (void)routeChanged:(NSNotification *)sender {
    [self sendInformations];
}

- (void)sendInformations {
    AVAudioSessionRouteDescription *currentRoute = [[AVAudioSession sharedInstance] currentRoute];
    NSMutableArray *devices = [NSMutableArray array];
    for (AVAudioSessionPortDescription * output in currentRoute.outputs) {
        NSDictionary *device = @{
          @"name": output.portName,
          @"portType" : output.portType,
          @"id": output.UID
        };
        [devices addObject: device];
    }
    [self sendEventWithName:@"onConnectedDevices" body:@{
      @"devices": devices,
    }];
}

- (NSArray<NSString *> *)supportedEvents {
    return @[@"onConnectedDevices"];
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

@end
