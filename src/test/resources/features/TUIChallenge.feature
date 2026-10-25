Feature: TUI Challenge login and holiday browsing
  Validate login, hotel listing navigation, and holiday content in the Android app.

  @smoke @regression
  Scenario: Login displays the all items catalog
    When I log in with test data "validLogin"
    Then the all items catalog is displayed

  @regression
  Scenario: Login without a password shows validation
    When I submit login with test data "loginWithoutPassword" without a password
    Then the password validation message is "Required"

  @regression
  Scenario Outline: A hotel is visible after searching its listing
    When I log in with test data "<dataset>"
    And I open the listing from test data "<dataset>"
    Then the hotel from test data "<dataset>" is displayed

    Examples:
      | dataset          |
      | allListingHotel  |
      | hotelsTabHotel   |

  @smoke @regression
  Scenario: Holidays tab displays its first holiday
    When I log in with test data "holidayBrowse"
    And I open the Holidays tab
    Then the first holiday is displayed