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

		ps.keywordSearch('롯데월드', (data, status, pagination) => placesSearchCB(data, status, pagination, map));

        // 우선 home 경로 접속 시, 접속자의 위도, 경도를 바탕으로 1km 이내에 있는 실종 동물 위치를 가져와야 하는데 이떄 실종동물들의 위치는 주소로 등록되어 있기 때문에 바로 조회가 불가능
        // 그렇기 떄문에 주소를 위도 경도롤 바꾸어주는 geocoding API 필요, 전부 다 순회하지 않고 도별로 카테고리 구분해서 가져오는게 좋을 듯
        // 그럼 미리 테이블을 나눠놔야 겠네

        // 경기, 서울, 경북, 경남, 등 이런식으로 테이블을 나누어서 저장해두고
        // 현재 위도 경도를 바탕으로 주소 변환 후 주소에 해당하는 테이블에서 하나씩 위도 경도 변환 후 가져온다? 혹은 저장할 때 위도 경도를 저장한다..?
        // 후자가 나은 것 같기는 한데 우선 테이블을 따로 뺴서

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