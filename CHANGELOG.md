# 🧾 Changelog — TradeOffer Price System Adjustment
## [1.0.3] — Added max book level for the Librarian profession
- Introduced a functionality to always get the highest-level enchanted books from Librarians.



## [1.0.3] — Change in handling of `uses` and `maxUses`

### 🔁 Previous behavior
- Each `TradeOffer` was previously configured with `maxUses = Integer.MAX_VALUE` to make trades effectively infinite.
- This approach caused several unintended effects:
    - The `updateDemandBonus()` calculation produced extremely negative or unstable values.
    - Prices appeared to change randomly.
    - Trades never became disabled, but the demand system stopped functioning as intended.

### ⚙️ New implementation
- The modification of `maxUses` has been replaced with a non-invasive Mixin introducing an internal counter called `virtualUses`.
- The original `uses` value is no longer modified; instead, `virtualUses` tracks how many times a trade has been used.
- Price growth and demand updates now rely on `virtualUses`, ensuring predictable and consistent behavior.

### 💡 Benefits
- Trades remain infinite without corrupting demand or causing integer overflow.
- Price scaling remains functional and reacts logically to trade usage.
- Maintains full compatibility with vanilla systems and future versions.
- Reduces the likelihood of data corruption or incompatibility because:
    - The Mixin only changes **behavior**, not **persistent villager data**.
    - Villager save data remains unaltered, minimizing potential update issues.

### 🧠 Result
| Aspect | Before (`maxUses = Integer.MAX_VALUE`) | Now (Mixin with `virtualUses`) |
|--------|----------------------------------------|--------------------------------|
| Trade disabling | Never occurred | Optional or controlled |
| Integer overflow | Possible | Impossible |
| Price variation | Erratic | Stable and predictable |
| Data modification | Alters villager data | Leaves villager data intact |
| Compatibility with future updates | Low | High |
| Maintenance | Complex | Simple and modular |

✅ **Conclusion:**
By replacing the `maxUses` override with a controlled `virtualUses` system, infinite trades are maintained safely, price behavior is stabilized, and villager data remains untouched—significantly reducing the risk of bugs or data conflicts in future game updates.

