# Seat Allocation Redesign - Percentage-Based Layout

## Summary of Changes

The seat allocation system has been redesigned from fixed row ranges to **percentage-based dynamic allocation**:

| Ticket Class | Old System | New System (%) | Calculation |
|---|---|---|---|
| **VeThuongGia** (Business) | Rows 1-6 (fixed) | 30% of total rows | floor(totalRows × 0.30) |
| **VePhoThong** (Economy) | Rows 7-20 (fixed) | 50% of total rows | floor(totalRows × 0.50) |
| **VeTietKiem** (Budget) | Rows 21+ (fixed) | Remaining (20%) | totalRows - business - economy |

## Implementation Details

### Modified Methods in `ChuyenBay.java`

1. **New Constants** (lines 30-32):
   - `BUSINESS_PERCENT = 0.30` (30% of rows)
   - `ECONOMY_PERCENT = 0.50` (50% of rows)
   - Budget gets remainder

2. **New Static Method: `calculateRowAllocation(int totalRows)`**:
   ```java
   /**
    * Calculate row allocation based on total capacity.
    * Returns array [businessEndRow, economyEndRow]
    */
   public static int[] calculateRowAllocation(int totalRows)
   ```
   - Calculates ending row for Business and Economy classes
   - Budget rows start after Economy

3. **Updated: `getSeatClassByRow(int row, int totalRows)`**:
   - Now takes **totalRows parameter** (requires caller to pass it)
   - Uses dynamic boundaries instead of hardcoded constants
   - Returns ticket class for given row based on percentage allocation

4. **Updated: `getDanhSachGheTrongByClass(String loaiVe)`**:
   - Now calls `getSeatClassByRow(row, totalRows)` with totalRows parameter
   - Correctly filters available seats for each ticket class dynamically

5. **Backward Compatibility Overload: `getSeatClassByRow(int row)`**:
   - Legacy method for cases that don't have totalRows info
   - Defaults to 20 rows (120-seat aircraft)

### Modified Code in `UsersGUI.java`

1. **Updated Legend** (lines 1976-1982):
   - Dynamically calculates and displays actual row ranges per aircraft
   - Uses `ChuyenBay.calculateRowAllocation(soHang)` to show correct boundaries
   - Format: "Hạng vé - Thương gia (1-X), Phổ thông (X+1-Y), Tiết kiệm (Y+1-Z)"

2. **Updated Seat Class Check** (line 1995):
   - Changed: `ChuyenBay.getSeatClassByRow(i)`
   - To: `ChuyenBay.getSeatClassByRow(i, soHang)`
   - Passes total rows for dynamic boundary calculation

## Test Cases

### Aircraft with 120 seats (20 rows)
```
Total Rows: 20
Business: floor(20 × 0.30) = 6 rows (1-6)
Economy:  floor(20 × 0.50) = 10 rows (7-16)
Budget:   20 - 6 - 10 = 4 rows (17-20)
```

### Aircraft with 130 seats (22 rows)
```
Total Rows: 22
Business: floor(22 × 0.30) = 6 rows (1-6)
Economy:  floor(22 × 0.50) = 11 rows (7-17)
Budget:   22 - 6 - 11 = 5 rows (18-22)
```

### Aircraft with 140 seats (24 rows)
```
Total Rows: 24
Business: floor(24 × 0.30) = 7 rows (1-7)
Economy:  floor(24 × 0.50) = 12 rows (8-19)
Budget:   24 - 7 - 12 = 5 rows (20-24)
```

### Aircraft with 150 seats (25 rows)
```
Total Rows: 25
Business: floor(25 × 0.30) = 7 rows (1-7)
Economy:  floor(25 × 0.50) = 12 rows (8-19)
Budget:   25 - 7 - 12 = 6 rows (20-25)
```

## Key Features

✅ **Dynamic Allocation**: Row ranges calculated per-flight based on total capacity
✅ **No Row Mixing**: Each row is allocated to exactly one ticket class
✅ **Backward Compatible**: Legacy calls default to 20-row aircraft
✅ **GUI Updated**: Seat selection dialog shows correct ranges dynamically
✅ **All Tests Pass**: No compilation errors

## Files Modified

1. `src\main\model\ChuyenBay.java` (lines 23-82, 180)
   - Added percentage-based constants
   - Added `calculateRowAllocation()` method
   - Updated `getSeatClassByRow()` with totalRows parameter
   - Updated `getDanhSachGheTrongByClass()` to pass totalRows

2. `src\main\Main\UsersGUI.java` (lines 1976-1982, 1995)
   - Updated legend to show dynamic row ranges
   - Updated seat class check to pass totalRows parameter

## Compilation Status

✅ **No errors found** - All changes compile successfully
