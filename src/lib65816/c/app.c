#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <jni.h>
#include "app.h"
#include "cpu.h"
#include "cpuevent.h"

// jmethodID consMid;
// jmethodID writeMemMid;
// jmethodID readMemMid;
// jmethodID hardwareUpdateMid;
// jmethodID loadPgzMid;
// JNIEnv         *env;
// jclass          mainClass;
// jobject   javaApp;

// void EMUL_hardwareUpdate(word32 timestamp) {
//   //(*env)->CallStaticVoidMethod(env, cls, hardwareUpdateMid, timestamp);
// }

// void EMUL_handleWDM(byte opcode, word32 timestamp) {
//   // TODO create a java method to call and call it from here
// }

// byte MEM_readMem(word32 address, word32 timestamp, word32 emulFlags) {
//   return (byte) (*env)->CallStaticShortMethod(env, mainClass, readMemMid, address, timestamp);
// }

// void MEM_writeMem(word32 address, byte b, word32 timestamp) {
//   (*env)->CallStaticVoidMethod(env, mainClass, writeMemMid, address, b, timestamp);
// }

int main (int argc, char *argv[]) {
//  puts("hello from C++!\n");
//
//	JavaVM         *vm;
//	JavaVMInitArgs  vm_args;
//	jint            res;
//	jstring         jstr;
//	jobjectArray    main_args;
//
//	vm_args.version  = JNI_VERSION_1_8;
//  JavaVMOption options[1];
//  options[0].optionString = "-Djava.class.path=/Users/rebecca/Developer/bemuse/java/app/build/libs/app.jar";
//  vm_args.options = options;
//	vm_args.nOptions = 1;
//  vm_args.ignoreUnrecognized = JNI_TRUE;
//
//	res = JNI_CreateJavaVM(&vm, (void **)&env, &vm_args);
//	if (res != JNI_OK) {
//		printf("Failed to create Java VMn");
//		return 1;
//	}
//
//	cls = (*env)->FindClass(env, "emu/App");
//	if (cls == NULL) {
//		printf("Failed to find App class\n");
//		return 1;
//	}
//
//  consMid = (*env)->GetMethodID(env, cls, "<init>", "()V");
//  if (consMid == NULL) {
//    printf("couldn't find emu/App consMid\n");
//    return 1;
//  }
//
//  javaApp = (*env)->NewObject(env, cls, consMid);
//  if (javaApp == NULL) {
//    printf("couldn't create javaApp instance\n");
//    return 1;
//  }
//
//  puts("created App class instance, hopefully screen now shows.\n");
//  puts("sleeping for 60 seconds");
//
//  sleep(60);


  // hardwareUpdateMid = (*env)->GetStaticMethodID(env, cls, "hardwareUpdate", "(J)V");
  // if (hardwareUpdateMid == NULL) {
  //   printf("couldn't find hardwareUpdate hardwareUpdateMid\n");
  //   return 1;
  // }

  // writeMemMid = (*env)->GetStaticMethodID(env, cls, "writeMemory", "(ISJ)V");
  // if (writeMemMid == NULL) {
  //   printf("couldn't find writeMemory writeMemMid\n");
  //   return 1;
  // }

  // readMemMid = (*env)->GetStaticMethodID(env, cls, "readMemory", "(IJ)S");
  // if (readMemMid == NULL) {
  //   printf("couldn't find readMemory readMemMid\n");
  //   return 1;
  // }

  // loadPgzMid = (*env)->GetStaticMethodID(env, cls, "loadPgz", "(Ljava/lang/String;)V");
  // if (loadPgzMid == NULL) {
  //   printf("couldn't find loadPgz loadPgzMid\n");
  //   return 1;
  // }

  // // wake up the java side
  // MEM_readMem(0x0000, 0, 0);

  // // process command line options
  // if (argc > 1) {
  //   for (int i = 1; i < argc; i++) {
  //     printf("load file: '%s'\n", argv[i]);
  //     jstring jstr = (*env)->NewStringUTF(env, argv[i]);
  //     (*env)->CallStaticVoidMethod(env, cls, loadPgzMid, jstr);
  //     (*env)->ReleaseStringUTFChars(env, jstr, NULL);
  //   }
  // }

  // int resetLo = MEM_readMem(0xFFFC, 0, 0);
  // int resetHi = MEM_readMem(0xFFFD, 0, 0);

  // printf("on reset, cpu will jump to: %04x.\n", resetHi * 256 + resetLo);

  // //puts("press enter to start the emulator\n");
  // //getchar();

  // CPUEvent_initialize();
  // //CPU_setUpdatePeriod(41943);     // 4Mhz
  // CPU_setUpdatePeriod(10000);
  // //CPU_setTrace(1);
  // CPU_reset();
  // CPU_run();

  return 0;
}
