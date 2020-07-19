import 'package:flutter/material.dart';
import 'package:flutter_audio_notifications/flutter_audio_notifications.dart';

void main() => runApp(new MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String status = 'hidden';

  @override
  void initState() {
    super.initState();

    FlutterAudioNotifications.setListener('pause', () {
      setState(() => status = 'pause');
    });

    FlutterAudioNotifications.setListener('play', () {
      setState(() => status = 'play');
    });

    FlutterAudioNotifications.setListener('next', () {});

    FlutterAudioNotifications.setListener('prev', () {});

    FlutterAudioNotifications.setListener('select', () {});
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: const Text('Plugin example app'),
        ),
        body: new Center(
            child: Container(
          height: 250.0,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: <Widget>[
              FlatButton(
                  child: Text('Show notification'),
                  onPressed: () {
                    FlutterAudioNotifications.showNotification(
                        title: 'Title', author: 'Song author');
                    setState(() => status = 'play');
                  }),
              FlatButton(
                  child: Text('Update notification'),
                  onPressed: () {
                    FlutterAudioNotifications.showNotification(
                        title: 'New Title',
                        author: 'New Song author',
                        isPlaying: false);
                    setState(() => status = 'pause');
                  }),
              FlatButton(
                  child: Text('Hide notification'),
                  onPressed: () {
                    FlutterAudioNotifications.hideNotification();
                    setState(() => status = 'hidden');
                  }),
              Text('Status: ' + status)
            ],
          ),
        )),
      ),
    );
  }
}
