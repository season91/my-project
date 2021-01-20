/**
 * 
 */
(() => {
	const API_KEY = '91b3722fa7a6f0b760cbcee5c2e9fb16';
	const WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?";
	const weather = document.querySelector('.weather-text');

	let leglng = () => {
		return new Promise((resolve, reject) => {
			let coord = {};
			navigator.geolocation.getCurrentPosition((position) => {

				/*지오로케이션을 통해 위도경도 받아서 넘겨주기*/
				coord.lat = position.coords.latitude;
				coord.lon = position.coords.longitude;
				resolve(coord);
			});
		});
	};

	let saveWeather = (location, temp) => {
		let weatherObj = {};
		let maxAge = new Date();
		maxAge = maxAge.setHours(maxAge.getHours() + 1);

		weatherObj.loc = location;
		weatherObj.temp = temp;
		weatherObj.maxAge = maxAge;

		localStorage.setItem("weather", JSON.stringify(weatherObj));
	}

	let getWeather = () => {
		leglng()
			.then((coord) => {
				let url = `${WEATHER_URL}lat=${coord.lat}&lon=${coord.lon}&appid=${API_KEY}&units=metric`;
				fetch(url)
					.then(response => response.json())
					.then(json => {
						saveWeather(json.name, json.main.temp);
						loadWeather(json.name, json.main.temp);
			})
		})
	}


	function loadWeather(location, temp) {
		weather.innerHTML = `${temp}℃ @ ${location}`;
	};

	let init = () => {
		let parsedWeather = JSON.parse(localStorage.getItem("weather"));
		let now = new Date();
		/*parsedWeather가 존재한다면*/
		if (parsedWeather) {
			/*존재하고 기간만료 확인*/
			if (now > parsedWeather.maxAge) {
				getWeather();
			} else {
				loadWeather(parsedWeather.loc, parsedWeather.temp);
			}
		} else {
			getWeather();
		}
	}
	
	init();
})();
