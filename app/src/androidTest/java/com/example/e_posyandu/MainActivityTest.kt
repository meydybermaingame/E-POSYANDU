package com.example.e_posyandu

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testMainActivityLaunches() {
        // Verify that the main activity launches successfully
        composeTestRule.onNodeWithText("Data Balita").assertExists()
    }

    @Test
    fun testBottomNavigationExists() {
        // Verify that all bottom navigation items exist
        composeTestRule.onNodeWithText("Data Balita").assertExists()
        composeTestRule.onNodeWithText("Input").assertExists()
        composeTestRule.onNodeWithText("Pertumbuhan").assertExists()
        composeTestRule.onNodeWithText("Ekspor").assertExists()
    }

    @Test
    fun testNavigationToInputScreen() {
        // Navigate to Input screen
        composeTestRule.onNodeWithText("Input").performClick()
        
        // Verify we're on the Input screen
        composeTestRule.onNodeWithText("Input Data Balita").assertExists()
        composeTestRule.onNodeWithText("Form Pendaftaran Balita").assertExists()
    }

    @Test
    fun testNavigationToPertumbuhanScreen() {
        // Navigate to Pertumbuhan screen
        composeTestRule.onNodeWithText("Pertumbuhan").performClick()
        
        // Verify we're on the Pertumbuhan screen
        composeTestRule.onNodeWithText("Pertumbuhan Balita").assertExists()
    }

    @Test
    fun testNavigationToExportScreen() {
        // Navigate to Export screen
        composeTestRule.onNodeWithText("Ekspor").performClick()
        
        // Verify we're on the Export screen
        composeTestRule.onNodeWithText("Ekspor Data CSV").assertExists()
    }

    @Test
    fun testDataBalitaScreenDefaultState() {
        // Verify default state of Data Balita screen
        composeTestRule.onNodeWithText("Data Balita").assertExists()
        
        // Check if search bar exists
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
    }

    @Test
    fun testAddButtonExistsInDataBalitaScreen() {
        // Verify that add button exists in the top bar
        composeTestRule.onNodeWithContentDescription("Tambah Balita").assertExists()
    }
} 