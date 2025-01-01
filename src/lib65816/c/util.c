#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "cpu.h"

char *toHex(unsigned long value, unsigned int digits)
{
	static char buffer[16];
	unsigned int offset = sizeof(buffer);;

	buffer[--offset] = 0;
	while (digits-- > 0) {
		buffer[--offset] = "0123456789ABCDEF"[value & 0xf];
		value >>= 4;
	}
	return (&(buffer[offset]));
}

void hexDump(char *memory, int offset, int length) {
  char buffer[256];
  char ascii[17] = "";
  for (int i = 0; i < 256; i++)
    buffer[i] = 0;

  for (int i = 0; i < length; i += 16) {
    sprintf((char *) buffer, "%04x: ", offset + i);
    for (int j = 0; j < 16; j++) {
      byte b = memory[offset + j + i];
      sprintf((char *) (6 + buffer + j*3), "%02x ", b);

      if (b >= 0x20 && b < 0x7f) {
        ascii[j] = b;
      } else {
        ascii[j] = '.';
      }
    }
    strcat(buffer, ascii);
    puts(buffer);
  }
}

// int main() {
//   char buffer[65];
//   for (int i = 0; i < 64; i++) {
//     buffer[i] = 'A' + i;
//   }
//   buffer[64] = '\0';

//   //hexDump( (const char *) buffer, 0, 64);
//   // for (int i = 0; i < 64; i++) {
//   //   printf("%d = '%c'\n", i, buffer[i]);
//   // }

//   // puts(buffer);
//   hexDump(buffer, 16, 48);
// }