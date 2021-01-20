/**
 * 비동기통신으로 백그라운드 이미지 바꿔주기
 */

(() => {
	const UNSPLASH_API_KEY = 'b7MZsws1lGqLrzuGtt4rVnMy0TEYnwMeGheEhKSn8m4';
	const UNSPLASH_URL = 'https://api.unsplash.com//photos/random/?';
	const body = document.querySelector('body');
	const locationContainer = document.querySelector('.location-text');

	let getBackground = () => {
		let url = `${UNSPLASH_URL}client_id=${UNSPLASH_API_KEY}&query=landscape&orientation=landscape`;
		fetch(url)
			.then(response => response.json())
			.then(json => {
				let imageUrl = json.urls.full;
				let desc = json.alt_description;;
				
				if(desc){
					saveBackground(imageUrl, desc);
					paintBackground(imageUrl, desc);
				} else{
					//desc가 없다면 있는 배경화면올때까지 재귀돌려줄것
					getBackground();
				}	
		})
	};
	
	
	let saveBackground = (imageUrl, desc) => {
		// 이미지에 만기일자를 추가. 
		let maxDate = new Date();

		maxDate.setDate(maxDate.getDate() + 1);

		const imageObject = {
			url: imageUrl,
			maxDate: maxDate,
			desc: desc
		};
		// 이미지정보를 가지고있는 새로운 로컬스토리지 생성
		// getBackground에서 불렀을 때 저장을 해주고 loadBackground에서 maxDate 판단해서 새로 받을지 유지할지 결정
		localStorage.setItem("bg", JSON.stringify(imageObject));
	}
	// 하루 1건 통신 판단기준은 만약 최근 api 통신으로부터 하루가 지난 상황이라면 새롭게 통신을 해서,
	// 새 이미지를 불러오고 그렇지 않다면 localStorage에 저장한 이미지로 배경이미지를 사용한다.

	let paintBackground = (url, desc) => {
		let savedImage = localStorage.getItem("bg");
		body.style.backgroundImage = `url(${url})`;
		locationContainer.innerHTML = desc;
	}


	let loadBackground = (savedImage) => {
		let parsedImg = JSON.parse(savedImage);
		let today = new Date();

		//만기일자가 지났다면 이미지 새로 받아오자!
		if (today > parsedImg.maxDate) {
			getBackground();
		} else {
			//만기일이 안지났다면 기존꺼 넣어주기
			paintBackground(parsedImg.url, parsedImg.desc);
		}

	}

	let init = () => {
		let savedImage = localStorage.getItem("bg");
		if (savedImage) {
			loadBackground(savedImage);
		} else {
			//스토리지에 bg가 없다면 첫 api라는거니까 바로 호출
			getBackground();
		}
	}

	init();

})();
