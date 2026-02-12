
# Traffic Light Application

## Overview
This project controls a **4-way traffic signal**. It has **8 phases**, and the lights change in a fixed loop. Each phase decides which direction gets **Green**, **Yellow**, or **Red**.

---
## Phase Cycle
The lights move in this order:
1 -> 2 -> 3 -> 4 -> 5 -> 6 -> 7 -> 8 -> back to 1

---
## Arrow Directions
- NS <-  : North–South Left Turn
- NS ^   : North–South Straight
- EW <-  : East–West Left Turn
- EW ->  : East–West Straight

---
## Phases and Colors
### 1. NS_LEFT_GREEN

NS <- GREEN
NS ^  RED
EW <- RED
EW -> RED


### 2. NS_LEFT_YELLOW

NS <- YELLOW
NS ^  RED
EW <- RED
EW -> RED


### 3. NS_STRAIGHT_PERMISSIVE

NS <- GREEN
NS ^  GREEN
EW <- RED
EW -> RED

### 4. NS_STRAIGHT_YELLOW

NS <- YELLOW
NS ^  YELLOW
EW <- RED
EW -> RED


### 5. EW_LEFT_GREEN

NS <- RED
NS ^  RED
EW <- GREEN
EW -> RED


### 6. EW_LEFT_YELLOW

NS <- RED
NS ^  RED
EW <- YELLOW
EW -> RED


### 7. EW_STRAIGHT_PERMISSIVE

NS <- RED
NS ^  RED
EW <- GREEN
EW -> GREEN


### 8. EW_STRAIGHT_YELLOW

NS <- RED
NS ^  RED
EW <- YELLOW
EW -> YELLOW


---
## Simple Phase Meaning
- **NS Left Green** → Only NS left turn moves
- **NS Left Yellow** → NS left turn is finishing
- **NS Straight Permissive** → NS straight moves; left turn if safe
- **NS Straight Yellow** → NS straight slowing
- **EW Left Green** → Only EW left turn moves
- **EW Left Yellow** → EW left turn finishing
- **EW Straight Permissive** → EW straight moves; left turn if safe
- **EW Straight Yellow** → EW straight slowing

---
## How the Service Works
- nextSequence() → moves to next phase and logs changes
- pause() → stops transitions
- resume() → restarts transitions
- getCurrentLightColors() → shows current lights
- getHistory() → shows all saved changes

---
## Project Structure

trafficlight
│
├── service
│   └── TrafficLightService.java
│
├── entity
│   └── TrafficLightHistory.java
│
├── model
│   ├── Direction.java
│   ├── LightColor.java
│   └── TrafficPhase.java
│
└── repository
    └── TrafficLightHistoryRepository.java
