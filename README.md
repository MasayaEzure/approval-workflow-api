# approval-workflow-api

## Purpose
Spring Bootで「申請・承認ワークフローAPI」を実装し、以下を説明できる状態を作る。
- Controller / Service / Repository の責務
- トランザクション境界（@Transactionalの意味と位置）
- JPAの基本挙動（CRUD、Lazy/Eager）
- 例外設計とログ設計

## 想定ユースケース
申請者が申請を作成し、承認者が承認/却下するワークフローを想定する。
金融/官公庁などの業務システムで一般的な「申請・承認」プロセスを最小構成で扱う。

## 機能要件（スコープ）
- 申請の下書き作成（DRAFT）
- 申請（SUBMITTED）
- 承認（APPROVED）
- 却下/差戻し（REJECTED）
- 一覧取得（自分の申請 / 承認待ち）
- 操作履歴の記録（監査用途）

## State Transitions
- DRAFT -> SUBMITTED
- SUBMITTED -> APPROVED
- SUBMITTED -> REJECTED  
※ 上記以外の遷移は業務例外とする

## Domain Model
Entities:
- User
- Request
- Approval
- RequestHistory

Relations:
- User 1:N Request
- Request 1:N Approval
- Request 1:N RequestHistory

## Design Decisions
### Layered Architecture
- Controller: 入出力（DTO変換）、バリデーション、認可の入口
- Service: 業務ロジック、状態遷移の保証、トランザクション境界
- Repository: 永続化アクセスの抽象化

### Transaction Boundary
状態変更（Request更新）と履歴追加（RequestHistory/Approval追加）を同一トランザクションで扱い、
途中失敗時に整合性が崩れないようにする。

### Error Handling
- 業務例外: 不正な状態遷移、権限不足、対象なし など
- システム例外: 想定外の障害  
業務例外はControllerAdviceでHTTPレスポンスに変換する。

## API Endpoints
- POST /requests
- POST /requests/{id}/submit
- POST /requests/{id}/approve
- POST /requests/{id}/reject
- GET /requests
- GET /approvals/pending

## Run (Local)
### Prerequisites
- Java xx
- Gradle or Maven

### Start
```bash
./gradlew bootRun
