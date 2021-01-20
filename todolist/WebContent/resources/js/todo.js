/**
 * 
	1. 사용자가 이전에 저장한 todolist가 localStorage 이미 있을 경우 화면출력
	2. 사용자가 todolist를 작성했을때 로컬스토리지 저장 후 작성한 todolist를 화면출력
		todolist는 객체배열 형태로 저장
	3. 사용자가 지우개 아이콘 클릭시 해당 todolist삭제하고 로컬스토리지에서도 삭제
	
	엔터로 등록했을때 새로고침되는 현상 수정
 */
/*객체 배열 선언*/
(() => {

	let todoArr = [];
	let todoContainer = document.querySelector('.frm_to-do');
	let list = document.querySelector('.list');

	let persistTodo = (todo) => {
		let todoObj = {};
		let todos = localStorage.getItem('todos');

		if (todos) {
			todoArr = JSON.parse(todos);
			if (todoArr.length > 21) {
				alert('그만 등록해주세요!');
				return;
			}
			// 고유한 인덱스를 가지는 객체를 넣어준다.
			todoObj.idx = todoArr.length;
		} else {
			todoObj.idx = 0;
		}

		todoObj.todoStr = todo;
		todoArr.push(todoObj);
		/*stringify해줘야 배열형태로 들어감*/
		localStorage.setItem('todos', JSON.stringify(todoArr));

	}

	let saveTodo = (event) => {
		// event의 기본동작을 이벤트를 취소시키는 메서드
		// submit의 이벤트의 기본동작을 취소시켜 새고를 막아준다
		event.preventDefault();

		let todo = document.querySelector('.to-do_add').value;

		if (todo) {
			persistTodo(todo);

		} else {
			alert("할 일을 입력해주세요!");
		}
		paintTodo();
		document.querySelector('.to-do_add').value = "";
		document.querySelector('.to-do_add').focus();
	}


	let paintTodo = () => {
		list.innerHTML = "";
		todoArr.forEach((e) => {
			let li = document.createElement('li');
			li.className = 'to-do';

			let deleteBtn = document.createElement('i');
			deleteBtn.className = 'fas fa-trash-alt';
			deleteBtn.style.margin = '0 1vw';
			deleteBtn.addEventListener('click', () => {
				deleteBtn.parentElement.outerHTML = '';

				//indexOf로 요소의 인덱스를 가져와서
				let idx = todoArr.indexOf(e);
				//splice로 삭제
				todoArr.splice(idx, 1);
				localStorage.setItem("todos", JSON.stringify(todoArr));
			});

			let todoText = document.createElement('span');
			todoText.innerHTML = e.todoStr;

			li.prepend(deleteBtn);
			li.append(todoText);
			list.appendChild(li);
		});
		
	}

	let init = () => {
		let todos = localStorage.getItem('todos');
		if (todos) {
			// todo-list를 화면에 출력
			todoArr = JSON.parse(todos);
			paintTodo();
		}
	}

	todoContainer.addEventListener('submit', saveTodo);

	init();
})();
