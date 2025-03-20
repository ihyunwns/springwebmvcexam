const container = document.getElementById('map'); //지도를 담을 영역의 DOM 레퍼런스
const move_to_my_coord = document.getElementById('move_current_position');
var infowindow = new kakao.maps.InfoWindow({zIndex:1});

function getCurrentPosition() {
    return new Promise((resolve, reject) => {
        navigator.geolocation.getCurrentPosition(resolve, reject);
    });
}

async function initMap() {
	try {
		const position = await getCurrentPosition();
		let latitude = position.coords.latitude;
		let longitude = position.coords.longitude;

		var mapContainer = document.getElementById('map'), // 지도를 표시할 div
			mapOption = {
				center: new kakao.maps.LatLng(latitude, longitude), // 지도의 중심좌표
				level: 3 // 지도의 확대 레벨
			};

		var map = new kakao.maps.Map(mapContainer, mapOption);
		var ps = new kakao.maps.services.Places();

		ps.keywordSearch('파담로 113', (data, status, pagination) => placesSearchCB(data, status, pagination, map));


	} catch (error){
		console.error("위치 정보를 가져올 수 없습니다", error);
	}
}


function placesSearchCB (data, status, pagination, map) {
    if (status === kakao.maps.services.Status.OK) {

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정하기위해
        // LatLngBounds 객체에 좌표를 추가합니다
        var bounds = new kakao.maps.LatLngBounds();

        for (var i=0; i<data.length; i++) {
            displayMarker(data[i], map);
            bounds.extend(new kakao.maps.LatLng(data[i].y, data[i].x));
        }

        // 검색된 장소 위치를 기준으로 지도 범위를 재설정합니다
        map.setBounds(bounds);
    }
}

function displayMarker(place, map) {

    // 마커를 생성하고 지도에 표시합니다
    var marker = new kakao.maps.Marker({
        map: map,
        position: new kakao.maps.LatLng(place.y, place.x)
    });

    // 마커에 클릭이벤트를 등록합니다
    kakao.maps.event.addListener(marker, 'click', function() {
        // 마커를 클릭하면 장소명이 인포윈도우에 표출됩니다
        infowindow.setContent('<div style="padding:5px;font-size:12px;">' + place.place_name + '</div>');
        infowindow.open(map, marker);
    });
}

document.addEventListener("DOMContentLoaded", initMap);