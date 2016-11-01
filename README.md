- Obrigatório instalação do MongoDB (https://docs.mongodb.com/manual/administration/install-community/)

- The Hal Browser disponível em: http://localhost:9000/

- Todas as consultas usam autenticação do tipo Basic: 
	Username: user
	Password: pass

- CRUDs:

	- Team (id, name)
		- Consultar todos: GET http://localhost:9000/teams
		- Consultar um time por id: GET http://localhost:9000/teams/{id}
		- Buscar time por nome: GET http://localhost:9000/teams/search/{name}
		- Ordenar consultar: GET http://localhost:9000/teams?sort=name
		- Paginação: GET http://localhost:9000/teams?size=2&page=1
		- Consultar membros do time: GET http://localhost:9000/teams/{id}/members
		- Deletar time: DELETE http://localhost:9000/teams/{id}
		- Adicionar time: POST http://localhost:9000/teams/
			{
				"name":"time"
			}
		- Editar time: PUT http://localhost:9000/teams/
			{
				"id": {id}
				"name":"time"
				
			}

	- Member (id, name)
			- Consultar todos: GET http://localhost:9000/members
			- Consultar um membro por id: GET http://localhost:9000/members/{id}
			- Buscar membro por nome: GET http://localhost:9000/members/search/{name}
			- Ordenar consultar: GET http://localhost:9000/members?sort=name
			- Paginação: GET http://localhost:9000/members?size=2&page=1
			- Deletar time: DELETE http://localhost:9000/members/{id}
			- Adicionar time: POST http://localhost:9000/members/
				{
					"name":"membro"
				}
			- Editar time: PUT http://localhost:9000/members/
				{
					"id": {id}
					"name":"membro"
					
				}

	+ GAPS/TODO:
		- Implementar e organizar testes funcionais abrangendo todas as regras de negócio
		- Implementar ordenação e paginação nas buscas por nome
		- JavaDocs para o HAL Browser
		- Swagger API
		- Embarcar o MongoDB (http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-nosql.html#boot-features-mongo-embedded)
				