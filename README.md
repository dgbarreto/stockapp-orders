# stockapp-orders

Kotlin Multiplatform (KMP) + Compose Multiplatform module of [StockApp](https://github.com/dgbarreto/stockapp-app) — an investment tracking app (learning project).

Domain + data (the user's buy/sell orders, client for [`stockapp-backend`](https://github.com/dgbarreto/stockapp-backend), `/orders` endpoints) and Compose screens for entering/listing orders, including a quick bottom sheet for fast buy/sell entry or declaring an existing position without detailing its full history.

`Order` is becoming the single source of truth for the portfolio: every create/update/delete recalculates the corresponding `Position` from scratch, transactionally, in the backend — replacing the manual position entry currently in [`stockapp-portfolio`](https://github.com/dgbarreto/stockapp-portfolio).

## Structure

- `orders/` — the only module in this repo, targeting Android (via `com.android.kotlin.multiplatform.library`) + iOS (static framework `Orders`), shared code in `orders/src/commonMain`.
- `sample/` + `sample-android/` — dev-only sample apps used to validate the module in isolation (login via `stockapp-auth` + a placeholder screen until the order screens exist).

## Status

Backend done and tested: `/orders` CRUD, ticker validation, and transactional recalculation of `Position` from the order history (rolled back automatically if a sale would exceed the current position). This KMP module's domain/data/presentation for `Order` are not implemented yet — that's the next guided step.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1

## Running

```
./gradlew :orders:build
./gradlew :orders:testAndroidHostTest
./gradlew :orders:iosSimulatorArm64Test
```

---

_Progress kept up to date manually as the project moves forward._
