#ifndef _GLUE_H_
#define _GLUE_H_

#include <stdbool.h>
#include <stdint.h>
#include <SDL.h>

#define WINDOW_TITLE "Vera Window"
#define MOUSE_GRAB_MSG " (Ctrl+M to end mouse/keyboard capture)"
#define MHZ machine_mhz

extern bool grab_mouse;
extern bool warp_mode;
extern uint8_t activity_led;
extern bool disable_emu_cmd_keys;
extern bool enable_midline;
extern uint8_t machine_mhz;

extern bool log_video;

extern SDL_KeyboardEvent keyboardEvent;

void psg_reset(void);
void pcm_reset(void);
void machine_dump(const char* msg);
void machine_reset();
void machine_nmi();
void machine_paste(const char* text);
void machine_toggle_warp();
void sdcard_attach();
void sdcard_detach();
void handle_keyboard(bool down, u_int16_t mod, SDL_Keycode sym, SDL_Scancode scancode);
void mouse_button_down(int num);
void mouse_button_up(int num);
void mouse_move(int x, int y);
void mouse_set_wheel(int y);
void mouse_send_state();

void audio_render();
void joystick_add(int index);
void joystick_remove(int index);
void joystick_button_down(int instance_id, uint8_t button);
void joystick_button_up(int instance_id, uint8_t button);

extern SDL_Event event;

#endif