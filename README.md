# approval-workflow-api

## Purpose

Spring Boot を用いて「申請・承認ワークフロー API」を実装し、以下の設計ポイントを説明できる状態を作る。

- Controller / Service / Repository の責務分離
- トランザクション境界（@Transactional の意味と位置）
- JPA の基本挙動（CRUD、Lazy/Eager）
- 状態遷移を伴う業務ロジック設計
- 業務例外とシステム例外の扱い

## 想定ユースケース

申請者が申請を作成し、承認者が承認または却下するワークフロー。

金融・官公庁などの業務システムで一般的な  
「申請 → 承認 → 完了 / 差戻し」プロセスを最小構成で扱う。

## 機能要件（スコープ）

- 申請の下書き作成（DRAFT）
- 申請提出（SUBMITTED）
- 承認（APPROVED）
- 却下 / 差戻し（REJECTED）
- 一覧取得（自分の申請 / 承認待ち）
- 操作履歴の記録（監査用途）

## State Transitions

- DRAFT -> SUBMITTED  
- SUBMITTED -> APPROVED  
- SUBMITTED -> REJECTED  

※ 上記以外の遷移は業務例外とする。

## Domain Model

### Entities

- User  
- Request  
- Approval  
- RequestHistory  

### Relations

- User 1:N Request  
- Request 1:N Approval  
- Request 1:N RequestHistory  

## Design Decisions

### Layered Architecture

- Controller  
  入出力（DTO変換）、バリデーション、認可の入口

- Service  
  業務ロジック、状態遷移の保証、トランザクション境界

- Repository  
  永続化アクセスの抽象化

### Transaction Boundary

Request の状態更新と  
RequestHistory / Approval の追加を  
同一トランザクションで処理する。

途中で失敗した場合に  
データの整合性が崩れないようにする。

### Error Handling

- 業務例外  
  不正な状態遷移、権限不足、対象データなし など

- システム例外  
  想定外の障害

業務例外は ControllerAdvice で  
HTTP レスポンスへ変換する。

## Authorization

ロールベースの権限制御を想定。

- Applicant  
  申請の作成・提出が可能

- Approver  
  承認・却下が可能

認証処理は学習用途のため簡略化している。

## API Endpoints

- POST /requests  
- POST /requests/{id}/submit  
- POST /requests/{id}/approve  
- POST /requests/{id}/reject  
- GET /requests  
- GET /approvals/pending  

## Example

### Submit Request

POST /requests/{id}/submit

Response:

```json
{
  "id": 1,
  "status": "SUBMITTED"
}
```

## Database

- H2 (in-memory)
- JPA (Hibernate)

ローカル起動時は自動で初期化される。

## Initial Data

起動時に以下のユーザーが登録される想定。

- user1: Applicant  
- user2: Approver  

## Run (Local)

### Prerequisites

- Java 17  
- Gradle 8.x  

### Start

```bash
./gradlew bootRun
```

## Testing

- Service 層のユニットテスト  
- API の統合テスト  

状態遷移や業務ルールが  
正しく動作することを検証する。