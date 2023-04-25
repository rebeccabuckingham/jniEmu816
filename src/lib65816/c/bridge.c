#include <jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include "emu_Main.h"
#include "cpu.h"
#include "cpuevent.h"

JNIEnv    *mainEnv;
jclass    mainClass;
jobject   mainObject;
jmethodID writeMemMid;
jmethodID readMemMid;
jmethodID hardwareUpdateMid;

JNIEXPORT void JNICALL Java_emu_Main_print(JNIEnv *env, jobject obj) {
	printf("Hello From *new* C World!\n");
	return;
}

void EMUL_hardwareUpdate(word32 timestamp) {
  (*mainEnv)->CallVoidMethod(mainEnv, mainObject, hardwareUpdateMid, timestamp);
}

byte MEM_readMem(word32 address, word32 timestamp, word32 emulFlags) {
  byte b = (byte) (*mainEnv)->CallShortMethod(mainEnv, mainObject, readMemMid, address, timestamp);
  return b;
}

void MEM_writeMem(word32 address, byte b, word32 timestamp) {
  (*mainEnv)->CallVoidMethod(mainEnv, mainObject, writeMemMid, address, b, timestamp);
}

void EMUL_handleWDM(byte opcode, word32 timestamp) { }

JNIEXPORT void JNICALL Java_emu_Main_runCpu(JNIEnv *env, jobject obj) {
  CPU_run();
}

JNIEXPORT void JNICALL Java_emu_Main_initCpu(JNIEnv *env, jobject obj) {
  mainObject = obj;
  mainEnv = env;

  printf("start of Java_emu_Main_initCpu()\n");

	mainClass = (*env)->FindClass(env, "emu/Main");
	if (mainClass == NULL) {
		printf("Failed to find Main class\n");
		return;
	}

  jmethodID mid = (*env)->GetMethodID(env, mainClass, "sayHelloJava", "()V");
  if (mid == NULL) {
    printf("Failed to find methodID for sayHelloJava()\n");
    return;
  }

  (*env)->CallVoidMethod(env, obj, mid);

  writeMemMid = (*env)->GetMethodID(env, mainClass, "writeMemory", "(ISJ)V");
  if (writeMemMid == NULL) {
    printf("Failed to find methodID for writeMemory()\n");
  }

  readMemMid = (*env)->GetMethodID(env, mainClass, "readMemory", "(IJ)S");
  if (readMemMid == NULL) {
    printf("Failed to find methodID for readMemory()\n");
  }

  hardwareUpdateMid = (*env)->GetMethodID(env, mainClass, "hardwareUpdate", "(J)V");
  if (hardwareUpdateMid == NULL) {
    printf("Failed to find methodID for hardwareUpdate()\n");
  }

  int resetLo = MEM_readMem(0xFFFC, 0, 0);
  int resetHi = MEM_readMem(0xFFFD, 0, 0);

  printf("on reset, cpu will jump to: %04x.\n", resetHi * 256 + resetLo);

  // rest of actual initialization
  CPUEvent_initialize();
  CPU_setUpdatePeriod(10000);
  CPU_setTrace(0);
  CPU_reset();

  printf("end of Java_emu_Main_initCpu()\n");

	return;
}
