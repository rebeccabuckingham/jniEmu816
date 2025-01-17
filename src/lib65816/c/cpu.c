/*
 * lib65816/cpu.c Release 1p1
 * See LICENSE for more details.
 *
 * Code originally from XGS: Apple IIGS Emulator (cpu.c)
 *
 * Originally written and Copyright (C)1996 by Joshua M. Thompson
 * Copyright (C) 2006 by Samuel A. Falvo II
 *
 * Modified for greater portability and virtual hardware independence.
 */

#include "config.h"
#include "cpu.h"

int	cpu_reset;
int	cpu_abort;
int	cpu_nmi;
word32	cpu_irq;
int	cpu_stop;
int	cpu_wait;
int	cpu_trace;

word32	cpu_update_period;
uint64_t  cpu_cycle_count;

void CPU_setUpdatePeriod(word32 period)
{
	cpu_update_period = period;
}

void CPU_setTrace(int mode)
{
	cpu_trace = mode;
}

void CPU_reset(void)
{
	cpu_reset = 1;
}

void CPU_abort(void)
{
	cpu_abort = 1;
}

void CPU_nmi(void)
{
	cpu_nmi = 1;
}

void CPU_addIRQ( word32 m )
{
	cpu_irq |= m;
}

void CPU_clearIRQ( word32 m )
{
	cpu_irq &= ~m;
}

void CPU_setRunAddress(word32 address)
{
    PC.W.PC = address & 0xffff;
    PC.B.PB = (address >> 16) & 0xff;
}
