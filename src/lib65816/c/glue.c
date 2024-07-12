#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>
#include <SDL.h>

// implement these later
void psg_reset(void) {

}

void pcm_reset(void) {

}

void machine_dump(const char* msg) {

}

void machine_reset() { }
void machine_nmi() { }
void machine_paste(const char* text) { }
void machine_toggle_warp() { }
void sdcard_attach() { }
void sdcard_detach() { }
void mouse_button_down(int num) { }
void mouse_button_up(int num) { }
void mouse_move(int x, int y) { }
void mouse_set_wheel(int y) { }
void mouse_send_state() { }
void audio_render() { }
void joystick_add(int index) { }
void joystick_remove(int index) { }
void joystick_button_down(int instance_id, uint8_t button) { }
void joystick_button_up(int instance_id, uint8_t button) { }

bool grab_mouse = false;
bool warp_mode = false;
bool disable_emu_cmd_keys = false;
bool enable_midline = false;

bool log_video = false;

// TODO this needs to be moved to smc.c 
uint8_t activity_led = 0;

uint32_t key_modifiers = 0;
uint32_t key_scancode = 0;

SDL_Event event;

uint8_t machine_mhz = 8;

void handle_keyboard(bool down, u_int32_t mod, SDL_Keycode sym, SDL_Scancode scancode) { 
  printf("handle_keyboard(%d, %d, %d, %d)\n", down, mod, sym, scancode);
  //key_modifiers = mod;
  //key_scancode = scancode;
}

extern bool video_init(int window_scale, float screen_x_scale, char *quality, bool fullscreen, float opacity);
extern bool video_step(float mhz, float steps, bool midline);
extern bool video_update(void);
extern void video_end(void);
extern void video_reset(void);
extern void video_write(uint8_t reg, uint8_t value);
extern uint8_t video_read(uint8_t reg, bool debugOn);

bool vera_init(int window_scale, int screen_x_scale, int mhz) {
  machine_mhz = mhz;
  return video_init(window_scale, (float) screen_x_scale, "best", 0, 1.0);
}

bool vera_step(int steps, bool midline) { return video_step((float) machine_mhz, (float) steps, midline); }
void vera_update() { video_update(); }
void vera_end() { video_end(); }
void vera_reset() { video_reset(); }
void vera_write(uint8_t reg, uint8_t value) { video_write(reg, value); }
uint8_t vera_read(uint8_t reg) { return video_read(reg, 0); }

bool vera_getEvent(void *buffer) {
  return SDL_PollEvent(buffer);
}
