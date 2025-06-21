# TryTask - Gerenciador de Tarefas

Um sistema simples e eficiente para gerenciamento de tarefas, desenvolvido em Java com interface gráfica moderna.

## Funcionalidades

- Login e autenticação de usuários
- Cadastro de tarefas
- Edição de tarefas existentes
- Exclusão de tarefas
- Armazenamento local em JSON
- Interface gráfica moderna com FlatLaf

## Requisitos

- Java 8 ou superior
- Bibliotecas (incluídas na pasta `lib/`):
  - FlatLaf
  - Gson

## Como Executar

1. Compile o projeto:
```bash
javac -cp "lib/*" -d out/production src/main/*.java src/models/*.java src/services/*.java src/interfaces/*.java src/data/*.java
```

2. Execute o programa:
```bash
java -cp "out/production;lib/*" main.TryTasks
```

## Credenciais Padrão

- Usuário: admin
- Senha: admin123

## Estrutura do Projeto

- `src/`: Código fonte
  - `models/`: Classes de modelo
  - `views/`: Interfaces gráficas
  - `services/`: Serviços e lógica de negócio
  - `controllers/`: Controladores
  - `utils/`: Utilitários
- `lib/`: Bibliotecas externas
- `bin/`: Arquivos compilados
- `resources/`: Recursos (ícones, etc) 