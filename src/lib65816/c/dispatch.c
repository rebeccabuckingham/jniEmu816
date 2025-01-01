/*
 * lib65816/dispatch.c Release 1p1
 * See LICENSE for more details.
 *
 * Code originally from XGS: Apple IIGS Emulator (dispatch.c)
 *
 * Originally written and Copyright (C)1996 by Joshua M. Thompson
 * Copyright (C) 2006 by Samuel A. Falvo II
 *
 * Modified for greater portability and virtual hardware independence.
 * 
 * Copyright (C) 2024 by Rebecca Buckingham
 * highly modified to integrate with bsx emulator.
 */

#define CPU_DISPATCH

#include "cpu.h"
#include "cpumicro.h"
#include "util.h"
#include <stdint.h>
#include <stdio.h>

int dispatch_quit = 0;
word32 runAddress = 0;

dualw   A;  /* Accumulator               */
dualw   D;  /* Direct Page Register      */
byte    P;  /* Processor Status Register */
int     E;  /* Emulation Mode Flag       */
dualw   S;  /* Stack Pointer             */
dualw   X;  /* X Index Register          */
dualw   Y;  /* Y Index Register          */
byte    DB; /* Data Bank Register        */

union {
#ifdef WORDS_BIGENDIAN
    struct { byte Z,PB,H,L; } B;
    struct { word16 Z,PC; } W;
#else
    struct { byte L,H,PB,Z; } B;
    struct { word16 PC,Z; } W;
#endif
    word32  A;
} PC;

duala       atmp,opaddr;
dualw       wtmp,otmp,operand;
int         a1,a2,a3,a4,o1,o2,o3,o4;
void        (**cpu_curr_opcode_table)();

extern int  cpu_reset,cpu_abort,cpu_nmi,cpu_irq,cpu_stop,cpu_wait,cpu_trace;
extern int  cpu_update_period;

extern void (*cpu_opcode_table[1310])();

uint64_t last_update, next_update;

#define RESET_OP    256
#define ABORT_OP    257
#define NMI_OP      258
#define IRQ_OP      259

void handleSignal(int type) {
  (**cpu_curr_opcode_table[type])();
}

void doUpdate() {
  E_UPDATE(cpu_cycle_count);
  last_update = cpu_cycle_count;
  next_update = last_update + cpu_update_period;
}

void CPU_init(void) {
  last_update = 0;
  next_update = cpu_update_period;
  cpu_cycle_count = 0;
  E = 1;
  F_setM(1);
  F_setX(1);
  CPU_modeSwitch();
}

void CPU_step(void) {
  if (cpu_cycle_count >= next_update) doUpdate();
  int opcode = M_READ_OPCODE(PC.A);
  PC.W.PC++;
  (**cpu_curr_opcode_table[opcode])();
}

void CPU_run(void) {
    CPU_setUpdatePeriod(1000);
    CPU_setTrace(0);
    cpu_reset = 0;
    cpu_abort = 0;
    cpu_nmi = 0;
    cpu_irq = 0;
    cpu_stop = 0;
    cpu_wait = 0;
    P = 0x34;
    E = 1;
    D.W = 0;
    DB = 0;
// TODO uncomment these lines to add runAddress support back in.
//    PC.B.PB = (runAddress >> 16);
    PC.B.PB = 0;
    S.W = 0x01FF;
    A.W = 0;
    X.W = 0;
    Y.W = 0;
//    PC.B.L = (runAddress & 0xff);
//    PC.B.H = ((runAddress >> 8) & 0xff);
    PC.B.L = 0x00;
    PC.B.H = 0xe0;
    CPU_modeSwitch();

    printf("Bank is: %02x, PC is %02x%02x\n", PC.B.PB, PC.B.H, PC.B.L);

  while (!dispatch_quit) {
    if (cpu_trace) CPU_debug();
    if (cpu_reset) { handleSignal(RESET_OP); continue; }
    if (cpu_abort) { handleSignal(ABORT_OP); continue; }
    if (cpu_nmi) { handleSignal(NMI_OP); continue; }
    if (cpu_irq) { handleSignal(IRQ_OP); continue; }
    if (cpu_wait) { cpu_cycle_count++; continue; }
    if (cpu_stop) continue;
    CPU_step();
  }
}

/* Recalculate opcode_offset based on the new processor mode */

void CPU_modeSwitch(void) {

    int opcode_offset;

    if (E) {
        opcode_offset = 1040;
    } else {
        if (F_getX) {
            X.B.H = 0;
            Y.B.H = 0;
        }
        opcode_offset = ((~P >> 4) & 0x03) * 260;
    }
#ifdef OLDCYCLES
    cpu_curr_cycle_table = cpu_cycle_table + opcode_offset;
#endif
    cpu_curr_opcode_table = cpu_opcode_table + opcode_offset;
}
