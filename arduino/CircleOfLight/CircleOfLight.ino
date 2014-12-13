#include <WS2812.h>
#include <HerkuleX.h>

#define STRIP_PIN (6)
#define STRIP_LEDS (212)

#define MOTOR_ID (253)

#define CMD_INIT (1)
#define CMD_START (10)
#define CMD_PIXELS (11)
#define CMD_STOP (12)

WS2812 strip(STRIP_LEDS);
cRGB pix;

static bool started = false;
static int16_t targetPosition;
static int16_t position;
static int16_t prevPosition;

// Settings
static int16_t startPosition;
static int16_t endPosition;
static int16_t motorSpeed;

void setup() {
	Serial.begin(115200);
	
	HerkuleX.beginSerial2(115200);
	HerkuleX.torqueOn(MOTOR_ID);
	movePos(0);
	
	strip.setOutput(6);
	clearPixels();
}

void loop() {
	if (started) {
		// Send motor position and receive pixel data
		doStuff();
	} else {
		// Idle, wait for the start signal
		if (Serial.available()) {
			int b = Serial.read();
			if (b == CMD_START) {
				// Received start signal, initialize everything
				prevPosition = 0;
				position = 0;
				targetPosition = 0;
				started = true;
				movePos(startPosition);
			} else if (b == CMD_INIT) {
				startPosition = readS16();
				endPosition = readS16();
				motorSpeed = readS16();
			}
		}
	}
}

void doStuff() {
	// Get motor position, save the previous position
	prevPosition = position;
	position = HerkuleX.getPos(MOTOR_ID);

	// Position bigger than 1023 is faulty, ignore that
	if (position > 1023)
		return;
	
	// Save bandwidth by sending position data only when it changes
	if (position != prevPosition) {
		// If target position hasn't reached endPosition, increase it
		if (targetPosition < endPosition) {
			targetPosition += motorSpeed;
			if (targetPosition > endPosition)
				targetPosition = endPosition;
			movePos(targetPosition);
		}
		
		// Send motor position data
		sendPosition();
		
		// Wait for pixels data
		while (!Serial.available())
			;

		// Read command header
		int b = Serial.read();
		if (b == CMD_PIXELS) {
			// Read pixels data and set them on LED strip
			updatePixels();
		} else if (b == CMD_STOP) {
			// Stopped drawing. Reset LED strip and motor
			started = false;
			clearPixels();
			movePos(startPosition);
		}
	}
}

// Send position data 
void sendPosition() {
	Serial.write((const uint8_t *) &position, sizeof(position));
	Serial.flush();
}

// Read and update pixels on LED strip
void updatePixels() {
	for (int i = 0; i < STRIP_LEDS; i++) {
		while (Serial.available() < 3)
			;
		pix.r = Serial.read();
		pix.g = Serial.read();
		pix.b = Serial.read();
		strip.set_crgb_at(i, pix);
	}
	strip.sync();
}

// Clear LED strip pixels
void clearPixels() {
	pix.r = pix.g = pix.b = 0;
	for (int i = 0; i < STRIP_LEDS; i++)
		strip.set_crgb_at(i, pix);
	strip.sync();
}

// Shortcut for HerkuleX's movePos
void movePos(int16_t pos) {
	HerkuleX.movePos(MOTOR_ID, pos, 255, HERKULEX_LED_GREEN);
}

int16_t readS16() {
	while (Serial.available() < 2)
		;
	return (Serial.read() << 8) | Serial.read();
}
