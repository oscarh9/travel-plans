## Bonus – Automated Tests & Code Improvements

This branch contains optional improvements that were not required by the original assignment.

The official solution requested in the challenge is available in the `main` branch.

---

### What’s included in this bonus branch
1. **Automated tests**
    - **Unit tests for** `TravelPlanService`
      - Validation rules
      - Add, update and delete behavior
      - Grouping logic
      - Case-insensitive and trimmed city compatibility
      - Edge cases (zero seats, invalid inputs, etc.)

   - **Servlet tests for** `TravelPlanServlet`
     - GET and POST flows
     - Create, update and delete actions
     - Error handling and validation scenarios
     - Grouped and non-grouped views

Mockito is used to isolate servlet behavior and focus on request/response logic.

2. **Constants extraction**
- All error messages, request parameters, attribute names and fixed strings have been centralized in a `TravelPlanConstants` class.
- This avoids duplication, improves readability and makes future changes safer and easier.

---

## Purpose of this branch

The goal of this bonus is to demonstrate:
- Good testing practices
- Clear separation of responsibilities
- Maintainable and readable code
- Professional approach to extending a legacy-style codebase without changing its behavior

---

## Important note

- This branch does not change the functional behavior of the application.
- No additional features were added.
- The solution in `main` remains fully compliant with the original requirements.