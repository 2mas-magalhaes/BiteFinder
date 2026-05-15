# BiteFinder Test Info

## Visão Geral

Este projeto tem 2 partes principais:

- `ByteFinder`: app Android
- `bitefinder-api`: backend PHP + páginas web de apoio

Script principal para arrancar tudo:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-bytefinder.ps1
```

Se o emulador ficar preso ou a app deixar de reabrir:

```powershell
powershell -ExecutionPolicy Bypass -File .\run-bytefinder.ps1 -RestartEmulator
```

## Contas De Teste

Estas contas existem no modo mock da app Android:

| Tipo | Email | Password | Nome | Role |
|---|---|---|---|---|
| Cliente | `goncalo@teste.com` | `123456` | `Gonçalo` | `cliente` |
| Restaurante | `restaurante@teste.com` | `123456` | `Chef Manuel` | `restaurante` |
| Restaurante | `resto@teste.com` | `123456` | `Chef Manuel` | `restaurante` |

Notas:

- As contas acima estão definidas no `MockDataProvider` da app.
- A conta `goncalo@teste.com / 123456` também é usada nos testes do endpoint de login PHP.
- As contas de restaurante mock vêm associadas aos restaurantes `3` e `7`.

## URLs Locais

Com o servidor PHP a correr:

- Site principal: `http://127.0.0.1:8000/`
- Registo de restaurante: `http://127.0.0.1:8000/auth_register_restaurante.html`
- Formulário para criar prato: `http://127.0.0.1:8000/add_menu.html`
- Login API JWT: `http://127.0.0.1:8000/api/auth/login_jwt.php`

## O Que Existe No Site

### `index.html`

Portal web de parceiro/restaurante com:

- login
- registo
- dashboard/menu
- gestão de pratos
- edição e eliminação de pratos
- destaque de pratos
- fallback para modo demo/mock

### `auth_register_restaurante.html`

Página simples para criar conta de restaurante via:

- `api/auth/register_restaurante.php`

### `add_menu.html`

Página simples para testar criação de prato via:

- `api/pratos/create.php`

## Estado Atual Do Backend

O servidor PHP local arranca, mas há uma limitação importante:

- o PHP local não tem `pdo_sqlsrv`
- por isso, endpoints que precisem de SQL Server podem falhar localmente

Na prática:

- o site pode abrir no browser
- a app Android pode compilar, instalar e abrir
- parte dos fluxos funciona em mock/fallback
- chamadas reais à base de dados podem não funcionar sem instalar o driver SQL Server para PHP

## Ficheiros Úteis

- Script de arranque: [run-bytefinder.ps1](C:/Users/tomas/Documents/GitHub/BiteFinder/run-bytefinder.ps1)
- App Android: [ByteFinder](C:/Users/tomas/Documents/GitHub/BiteFinder/ByteFinder)
- Backend/site: [bitefinder-api](C:/Users/tomas/Documents/GitHub/BiteFinder/bitefinder-api)
- Site principal: [index.html](C:/Users/tomas/Documents/GitHub/BiteFinder/bitefinder-api/index.html)
- Registo restaurante: [auth_register_restaurante.html](C:/Users/tomas/Documents/GitHub/BiteFinder/bitefinder-api/auth_register_restaurante.html)
- Criar prato: [add_menu.html](C:/Users/tomas/Documents/GitHub/BiteFinder/bitefinder-api/add_menu.html)
