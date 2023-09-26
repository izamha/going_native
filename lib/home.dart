import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class Home extends StatefulWidget {
  const Home({super.key});

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  // String _batteryLevel = 'Unknown battery level...';
  final String _callStatus = "Initiating a call with the self...";

  static const platform = MethodChannel('askfield.com/battery');

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              // Text(
              //   _batteryLevel,
              //   textAlign: TextAlign.center,
              //   style: const TextStyle(
              //     fontSize: 24,
              //     color: Colors.black,
              //   ),
              // ),
              // const SizedBox(
              //   height: 16,
              // ),
              Text(
                _callStatus,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 24,
                  color: Colors.black,
                ),
              ),
              // const SizedBox(
              //   height: 16,
              // ),
              // ElevatedButton(
              //   onPressed: _getBatteryLevel,
              //   child: const Text('Get Battery Level'),
              // ),
              const SizedBox(
                height: 32,
              ),
              ElevatedButton(
                onPressed: _initiateCall,
                child: const Text('Initiate a Call'),
              ),
            ],
          ),
        ],
      ),
    );
  }

  Future<void> _initiateCall() async {
    List<String> phoneNumbers = ["0787907590", "0739242725"];
    try {
      await platform.invokeMethod('initiateCall', phoneNumbers);
    } on PlatformException catch (e) {
      debugPrint("Error => ${e.message}");
    }
  }

  // Future<void> _getBatteryLevel() async {
  //   String batteryLevel;
  //   try {
  //     final int result = await platform.invokeMethod('getBatteryLevel');

  //     batteryLevel = 'Battery level at $result % .';
  //   } on PlatformException catch (e) {
  //     batteryLevel = "Failed to get battery level: '${e.message}'.";
  //   }
  //   setState(() {
  //     _batteryLevel = batteryLevel;
  //   });
  // }
}
