package com.example.treadmillassistant.backend

import com.example.treadmillassistant.ui.home.OnStartClickedListener
import com.google.android.material.tabs.TabLayout


const val SECONDS_IN_MINUTE = 60
const val SECONDS_IN_HOUR = 3600
const val MILLIS_IN_SECOND = 1000
const val DISTANCE_ROUND_MULTIPLIER = 100.0
const val DURATION_ROUND_MULTIPLIER = 10.0
const val SPEED_ROUND_MULTIPLIER = 10.0
const val TILT_ROUND_MULTIPLIER = 10.0
const val PACE_ROUND_MULTIPLIER = 10.0
const val PERCENTAGE_ROUND_MULTIPLIER = 10.0
const val DEFAULT_WORKOUT_START_SPEED = 15.0
const val DEFAULT_WORKOUT_START_TILT = 0.0
const val DEFAULT_MAX_SPEED = 30.0
const val DEFAULT_MIN_SPEED = 1.0
const val DEFAULT_MAX_TILT = 15.0
const val DEFAULT_MIN_TILT = -5.0
const val SPEED_TILT_INCREMENT = 0.1
const val DEFAULT_PHASE_SPEED = 0.0
const val DEFAULT_PHASE_DURATION = 0.0
const val DEFAULT_PHASE_TILT = 0.0
const val HOME_TAB_NAV_VIEW_POSITION = 0
const val TRAINING_HISTORY_NAV_VIEW_POSITION = 1
const val SETTINGS_TAB_NAV_VIEW_POSITION = 2
const val SEARCH_POPUP_LIST_SIZE = 10
const val SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT = 5
const val SELECT_TRAINING_PLAN_LIST_FIRST_LOAD_LIMIT = 10

var appStart = true

var tabLayout: TabLayout? = null
var startClicked: OnStartClickedListener? = null
var lastNavViewPosition = 0