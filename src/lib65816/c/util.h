#pragma once
#include "cpu.h"

char *toHex(unsigned long value, unsigned int digits);
void hexDump(char *memory, int offset, int length);