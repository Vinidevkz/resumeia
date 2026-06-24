
# Resume IA

 Obtenha feedbacks precisos sobre curriculos.


## 🚀 Sobre o projeto
 O Resume IA é uma plataforma de avaliações de curricuos com inteligência artificial. Os usuários podem fazer upload de seus curriculos e receber um feedback sobre o mesmo, envolvendo análise da formatação e sobre as informações contidas no mesmo. 


## ⚙️ Tecnologias utilizadas

**Cliente:** React.

**Servidor:** Java, Spring Framework e PostgreSQL.

## 📝 API Reference

## Usuário:

#### 🔒 Cadastro de usuário

```http
  POST /v1/auth/users/regiser
```
| Retorno |
| :-------- | 
| `Dados do usuário`      | 

#### 🔓 Login de usuário 

```http
  POST /v1/auth/users/login
```

| Retorno |
| :-------- | 
| `Token JWT`      |

#### 🔄 Alteração de dados do usuário 

```http
  POST /v1/auth/users/update
```
| Parametros | Type     |
| :-------- | :------- |
| `json com os dados do usuario`      | `user` |
| `id do usuário`      | `long` |

#### ❌ Deletar usuário 

```http
  POST /v1/auth/users/delete
```
| Parametros | Type     |
| :-------- | :------- |
| `id do usuário`      | `long` |



## Currículo:

#### ↗️ Envio de currículo

```http
  POST /v1/resumeia/resume/send
```

| Parametros | Type     |
| :-------- | :------- |
| `id`      | `long` |
| `token`      | `string` | 

| Retorno | Type |
| :-------- | :------|
| `feedback da ia`      | `string` | 




