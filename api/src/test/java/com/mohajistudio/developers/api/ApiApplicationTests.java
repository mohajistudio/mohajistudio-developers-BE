package com.mohajistudio.developers.api;

import com.mohajistudio.developers.infra.service.AzureOpenAiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests {

    @Autowired
    AzureOpenAiService azureOpenAiService;

    String content = """
            # Firebase Push Notification Custom Sound
            
                  푸시 알림의 알림음을 사용자 설정으로 만들게 되어 이를 해결하는 과정을 기록하였습니다. 먼저 라이브러리를 `Firebase Messaging` 과 `Flutter Local Notifications` 두 개를 사용하여 처리해주었습니다.
            
                  `flutter` 버전은 2.10.5입니다.
            
                  ```tsx
                  flutter_local_notifications: ^9.5.3+1
                  firebase_messaging: ^11.2.6
                  ```
            
                  두 개의 버전입니다. 버전이 다르다면 설정 방법 또한 다를 수 있습니다.
            
                  먼저 기존에 FCM(`Firebase Cloude Message`, 이하 FCM)을 사용하여 푸시 알림을 구현해두었고 `Local Notifications`는 세팅만 해두고 사용하지 않고 있었습니다. 기존 세팅에 관한건 작성하지 않겠습니다.
            
                  ## 기존 푸시 알림 방식
            
                  저희 서비스 같은 경우 로그인이 완료되고 `Home`화면을 불러올 때 FCM 서비스 부분을 `DI` 합니다. 이는 추후 정책에 따라 변할 수 있지만 로그인 이전의 사용자에게 푸시 알림을 보내는거에 의구심이 들어 로그인 후로 설정해두었습니다. 그렇게 한 번이라도 로그인을 한 유저라면 `Firebase`에서 `Token`을 발급받아 서버로 보냅니다. (앱을 삭제했다가 다시 설치하기 전에는 토큰을 계속 사용할 수 있습니다.) 그 `Token`을 통해 서버에서는 언제든지 앱으로 푸시 메시지를 보낼 수 있습니다. \s
            
                  FCM에는 `listener`가 있습니다. 알림에 대한 권한 요청을 받은 사용자를 체크하고 `foreground`, `background`, `terminated` 상태의 유저에 따라 푸시 알림을 처리합니다.\s
            
                  ## 첫 번째 시도
            
                  처음에는 이 `foreground`, `background`, `terminated`의 이벤트를 감지하여 이벤트가 발생할 경우 로컬 알림을 날리는 방식으로 처리해보았습니다. `foreground` 상태일 경우는 안드로이드든 iOS든 푸시 알림이 오지 않고 데이터 형식으로 받아 앱에서 처리해주는 방식입니다. 그렇기 때문에 푸시 알림을 받아 알림을 수신하는 네이티브로 이벤트를 발생시켜 `foreground`에서 푸시 알림과 사운드를 확인할 수 있게 해줍니다. 이 때 안드로이드의 경우 채널을 사용하는데 대한 설정에 반드시 중요도를 최대치로 등록해줘야 합니다.
            
                  ```tsx
                  NotificationDetails notificationDetails() => const NotificationDetails(
                          android: AndroidNotificationDetails('literacym_notification_channel', 'LiteracyM Notification Channel',
                              importance: Importance.max, //중요
                              channelDescription: 'LiteracyM Local Notification Channel',
                              sound: RawResourceAndroidNotificationSound('test'),
                              playSound: true),
                          iOS: IOSNotificationDetails(
                            sound: 'test.mp3',
                            presentAlert: true,
                            presentBadge: true,
                            presentSound: true,
                          ),
                        );
                  ```
            
                  그런데 모든게 성공했나 싶었을 때 뭔가 이상함을 느꼈습니다. 안드로이드 알림 부분에서 알림이 두 번 울리는 것이었습니다. FCM의 경우 `background` 상태일 때는 자동으로 알림이 기본값으로 울리게 되는 것을 알게 되었고 `background`에서 자동으로 알림이 울린 후 제가 한 번 더 로컬 알림을 날렸으니 두 번의 알림이 울리는 일이 일어났습니다. 그런데 이상하게도 iOS도 같은 방식으로 동작하지만 푸시 알림과 로컬 알림이 동시에 일어나면 로컬 알림 한 번만 작동해줘서 좋았습니다. (뒤에서 어떤 일이 일어나는지는 정확히 모르겠네요…) 그래도 이는 근본적인 해결이 아니었습니다. 저는 FCM에서 `Local Notifications`를 들어내고 FCM 자체에서 문제를 해결하려고 시도했습니다.
            
                  ## 두 번째 시도
            
                  열심히 구글에 삽질을하며 찾아본 결과
            
                  FCM을 전송하는 부분의 매개변수를 보니 `sound`, `playSound` 같은게 있길래 바로 적용해보았습니다. 이는 안드로이드에서는 효과가 없었으나 iOS는 바로 잘 작동하였습니다. 이로인해 iOS는 기존의 찝찝한 로컬 알림과 푸시 알림 두 개를 동시에 발생시켜 로컬 알림만 울리게 하는 방법보다 마음에 들었습니다. 그런데 같은 매개변수인데 안드로이드만 또 제대로 작동을 안 하니 다시 찾아보기 시작했는데 일단 FCM에 관한 정보도 얼마 없거니와 있어도 Android 자체 해결 이슈만 있었습니다. 답변들을 종합 해보면 안드로이드에서 알림 수신 채널을 만들어서 FCM의 매개변수로 채널 id를 입력해서 채널에 고유한 `sound`를 재생하라는 것이었습니다.\s
            
                  안드로이드 개발이 익숙하지 않아 이것저것 예제를 따라해보는데 같은 코드여도 플러터 프로젝트로 만들어진 앱이라서 그런지 곳곳에서 에러가 발생했고, 그걸 해결할 능력 또한 되지 않았습니다. 이런 저런 방법을 찾던 도중 기존 로컬 알림을 날리던 부분은 채널을 설정해서 쏘지 않았습니까? 그러면 그 로컬 알림을 날리던 채널로 푸시 알림을 날려보면 어떨까 라는 생각이 들어 날려봤더니 커스텀 알림음으로 소리가 나는겁니다! 그런데 이 채널의 경우 로컬에서 한 번이라도 그 채널로 로컬 알림을 발생시켜야 열리는데 정말 해결책이 이거 하나 밖에 없나 싶어 앱에 들어와서 알림을 한 번이라도 설정하면 테스트 알림 하나를 보내 채널을 열어줘야하나는 생각이 들었습니다.\s
            
                  <aside>
                  💡 채널이 한 번 열리면 핸드폰을 다시 껐다 키거나 시간이 지나도 유지됩니다. 앱을 삭제할 경우 초기화됩니다.
            
                  </aside>
            
                  이 또한 만족스럽지 않습니다. FCM과 `Local Notifications`가 `DI` 될 때 채널이 열리고 사운드 별로 채널을 만드는 것 까지가 제 목표입니다. 그럼 사운드 별로 테스트 알림을 한 번씩 다 날리는건 말이 안되니 `Local Notifications` 라이브러리를 열고 채널을 어떻게 여는지 뒤져보았습니다. 끝내 채널을 여는 메서드 부분을 찾았고 삭제하는 메서드도 찾았습니다. 이렇게 존재하는 메서드는 `pub.dev`에는 존재하지 않고 제가 구글링 했던 어느곳에도 알려지지 않은 메서드였습니다. 이는 매우 성공적이었고 이제 사운드 별로 다른 채널을 만들고 이를 테스트 하는 일만 남았습니다.
            
                  테스트를 해보니 채널은 한 번 열리면 닫히지 않았습니다. 그래서 초기화 단계에서 채널들을 삭제하고 다시 채널을 열도록 코드를 구성했습니다.
            
                  ```tsx
                  Future<void> initializeChannels() async {
                      await removeChannels();
                      createChannels();
                    }
            
                    Future<void> removeChannels() async {
                      await _flutterLocalNotificationsPlugin
                          .resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()
                          ?.deleteNotificationChannel('literacym_notification_channel');
                      await _flutterLocalNotificationsPlugin
                          .resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()
                          ?.deleteNotificationChannel('literacym_notification_channel2');
                    }
            
                    void createChannels() {
                      _flutterLocalNotificationsPlugin.resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()?.createNotificationChannel(
                            const AndroidNotificationChannel('literacym_notification_channel', 'LiteracyM Notification Channel',
                                importance: Importance.max, playSound: true, sound: RawResourceAndroidNotificationSound('test')),
                          );
                      _flutterLocalNotificationsPlugin.resolvePlatformSpecificImplementation<AndroidFlutterLocalNotificationsPlugin>()?.createNotificationChannel(
                            const AndroidNotificationChannel('literacym_notification_channel2', 'LiteracyM Notification Channel',
                                importance: Importance.max, playSound: true, sound: RawResourceAndroidNotificationSound('test2')),
                          );
                    }
                  ```
            
                  테스트 코드로 `literacym_notification_channel`, `literacym_notification_channel2` 두 개의 사운드를 만들었는데 둘 다 하나의 사운드만 나오고 만약 이를 따로따로 분리해서 사용해보면 잘 작동하는 기이한 현상3가 나왔습니다. 이는 혹시나 하는 마음에 채널명을 숫자를 붙이는 것이 아닌 문자를 바꾸는 걸로 해결이 됐고 찝찝하지만 사운드별로 이름을 만들 생각이어서 넘어가기로 했습니다.\s
            
                  iOS의 경우 기존과 동일하게 `sound: “test.mp3”` 이 부분만 파일명을 바꿔주면 알아서 해당하는 알림음으로 변경하는 방법으로 사용하시면 됩니다.
            
                  마무리된 전문입니다.
            
                  ```tsx
                  //(POST)https://fcm.googleapis.com/fcm/send
                  {
                  	"to": "device_token",
                      "priority" : "high",
                  	"notification": {
                  		"body": "Firebase Cloud Message Body",
                  		"title": "Firebase Cloud Message Title",
                  		"subtitle": "Firebase Cloud Message Subtitle",
                      "android_channel_id": "literacym_notification_channel2",
                      "playSound": true,
                      "sound": "test2.mp3"
                  	},\s
                  	"data": {
                  		//페이로드 작성
                  	}
                  }
                  ```
            
                  **Android**
            
                  | android_channel_id | 원하는 사운드가 설정된 채널명 |
                  | --- | --- |
            
                  **iOS**
            
                  | sound | 사운드 파일명(기본값은 “default”) |
                  | --- | --- |
                  | playSound | true를 해주지 않으면 소리가 나지 않음 |
            """;

    @Test
    void openAITest() {
        String summary = azureOpenAiService.generateSummary(content);

        System.out.println("summary = " + summary);
    }
}
