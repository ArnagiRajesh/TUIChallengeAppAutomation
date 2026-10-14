Feature: TUI Challenge login and holiday browsing
  Validate login, catalogue navigation, and holiday content in the Android app.

  Scenario Outline: Login shows the all items catalogue
    When I log in with username "<username>", password "<password>", and date "<date>"
    Then the all items catalog is displayed

    Examples:
      | username | password | date     |
      | user1    | pass1    | 01041999 |

  Scenario Outline: Login without a password shows validation
    When I submit login with username "<username>" and date "<date>" without a password
    Then the password validation message is "Required"

    Examples:
      | username | date     |
      | user1    | 01041999 |

  Scenario Outline: A hotel can be found by scrolling
    When I log in with username "<username>", password "<password>", and date "<date>"
    And I scroll to hotel "<hotel>"

    Examples:
      | username | password | date     | hotel                       |
      | user1    | pass1    | 01041999 | Hotel: Melia White House    |

  Scenario Outline: Hotels tab is shown and can be scrolled
    When I log in with username "<username>", password "<password>", and date "<date>"
    Then the Hotels tab is displayed and I scroll to "<hotel>"

    Examples:
      | username | password | date     | hotel     |
      | user1    | pass1    | 01041999 | President |

  Scenario Outline: Holidays tab displays its first holiday
    When I log in with username "<username>", password "<password>", and date "<date>"
    And I open the Holidays tab
    Then the first holiday is displayed

    Examples:
      | username | password | date     |
      | user1    | pass1    | 01041999 |
