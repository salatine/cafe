# Café

A linguagem Café é uma linguagem de programação minimalista criada para simplificar o desenvolvimento de software. Ela possui uma sintaxe simples e intuitiva, tornando-a acessível para novatos, mas poderosa o suficiente para satisfazer as necessidades dos desenvolvedores experientes.

## Dependências
Para construir ou rodar o compilador, é necessário o Java 21 ou superior.

## Sintaxe

Aqui estão alguns exemplos básicos da sintaxe da linguagem Café:

```cafe
// Declaração de variável inteira
inteiro x = 1!

// Declaração de variável real
real y = 1.2!

// Função imprimir
imprimir(x * y)!
```

Note que todas as linhas em Café terminam com um ponto de exclamação, essa é a indicação de final de linha da linguagem.


## Construindo o compilador
1. Clone o repositório e execute o comando `javac -d out/ -sourcepath tcc/ tcc/*.java tcc/exceptions/*.java tcc/nodes/*.java tcc/tokens/*.java` e você terá o compilador construído no diretório `out/tcc`.
2. Caso deseja construir o arquivo JAR, execute o comando `jar -cvfe compiler.jar tcc.Compiler -C out/ .`.

## Rodando o compilador
É possível rodar o compilador de duas maneiras: 
1. Executando o comando `java tcc.Compiler arquivo.cafe` dentro da pasta `out`.
2. Executando o comando `java -jar compiler.jar arquivo.cafe` com o <a href="https://github.com/salatine/cafe-compiler/releases">arquivo JAR</a>.

Onde `arquivo.cafe` é o arquivo escrito em Café. Rodar o compilador gerará um arquivo com o mesmo nome, porém com a extensão .java.
