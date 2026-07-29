# stockapp-orders

Módulo KMP (Kotlin Multiplatform) + Compose Multiplatform do [StockApp](https://github.com/dgbarreto/stockapp-app) — app de acompanhamento de investimentos (projeto de estudo).

Domain + data (ordens de compra/venda do usuário, cliente do [`stockapp-backend`](https://github.com/dgbarreto/stockapp-backend), endpoints `/orders`) e telas Compose de lançamento/listagem de ordens, incluindo um bottom sheet rápido pra registro ágil de compra/venda ou declaração de posição já existente sem detalhar histórico.

## Estrutura

- `orders/` — único módulo do repo, alvo Android (via `com.android.kotlin.multiplatform.library`) + iOS (framework estático `Orders`), código comum em `orders/src/commonMain`.
- `sample/` + `sample-android/` — apps de exemplo, dev-only, pra validar o módulo isoladamente (login via `stockapp-auth` + tela de placeholder até as telas de ordens existirem).

## Status

**Fase 6 — Ordens e importação** (ver roadmap em `docs/roadmap.md` no repo de planejamento): scaffold criado a partir do template `stockapp-portfolio`, backend (`/orders`, recalculo transacional de `Position` a partir do histórico de ordens) já implementado e testado. Ainda sem domain/data/presentation de `Order` implementados neste módulo — próximo passo guiado.

## Stack

- Kotlin 2.4.0 · Compose Multiplatform 1.11.1 · AGP 9.0.1

## Rodando

```
./gradlew :orders:build
./gradlew :orders:testAndroidHostTest
./gradlew :orders:iosSimulatorArm64Test
```

---

_Progresso mantido manualmente conforme o projeto avança._
