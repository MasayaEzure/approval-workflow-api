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
- 一覧取得
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

- REQUESTER
  申請の作成・提出が可能

- APPROVER
  承認・却下が可能

- ADMIN
  管理者（現在の実装では未使用）

認証処理は学習用途のため簡略化している。
操作者の識別はリクエストボディの ID フィールドで行う。

## API Endpoints

| メソッド | パス | 説明 | レスポンス |
|---------|------|------|-----------|
| POST | /api/requests | 申請作成（DRAFT） | 201 Created |
| POST | /api/requests/{id}/submit | 申請提出（DRAFT -> SUBMITTED） | 200 OK |
| POST | /api/requests/{id}/approve | 承認（SUBMITTED -> APPROVED） | 200 OK |
| POST | /api/requests/{id}/reject | 却下（SUBMITTED -> REJECTED） | 200 OK |
| GET | /api/requests | 申請一覧取得 | 200 OK |
| GET | /api/requests/{id} | 申請詳細取得 | 200 OK |

## Example

### Create Request

```
POST /api/requests
Content-Type: application/json

{
  "title": "経費精算申請",
  "requesterId": 1
}
```

Response (201 Created):

```json
{
  "id": 1,
  "status": "DRAFT"
}
```

### Submit Request

```
POST /api/requests/1/submit
Content-Type: application/json

{
  "actorId": 1
}
```

Response (200 OK):

```json
{
  "id": 1,
  "status": "SUBMITTED"
}
```

### Approve Request

```
POST /api/requests/1/approve
Content-Type: application/json

{
  "approverId": 2,
  "comment": "承認します"
}
```

Response (200 OK):

```json
{
  "id": 1,
  "status": "APPROVED"
}
```

## Database

- PostgreSQL 16（Docker Compose で起動）
- JPA (Hibernate)
- DDL: `spring.jpa.hibernate.ddl-auto=update`（自動スキーマ生成）

## Run (Local)

### Prerequisites

- Java 17
- Gradle 8.x
- Docker / Docker Compose

### Start

1. PostgreSQL を起動する

```bash
docker compose up -d
```

2. アプリケーションを起動する

```bash
./gradlew bootRun
```

## Testing

現在はアプリケーション起動確認テスト（`contextLoads`）のみ実装済み。
今後以下のテストを追加予定。

- Service 層のユニットテスト（状態遷移、ロールチェック等）
- Controller 層の統合テスト（HTTP ステータスコード、バリデーション等）
