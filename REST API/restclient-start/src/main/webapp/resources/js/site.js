const uri = 'http://localhost:8090/restapi/Beer';
let beer = [];

function getItems() {
    fetch(uri)
        .then(response => response.json())
        .then(data => _displayItems(data))
        .catch(error => console.error('Unable to get items.', error));
}

function addItem() {
    const addNameTextbox = document.getElementById('add-name');
    const addNStyleTextbox = document.getElementById('add-style');
    const addBrandTextbox = document.getElementById('add-brand');

    const item = {
        complete: false,
        name: addNameTextbox.value.trim(),
        style: addNStyleTextbox.value.trim(),
        brand: addBrandTextbox.value.trim()
    };

    fetch(uri, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(item)
    })
        // .then(response => response.json())
        .then(() => {
            //console.log('added beer');
            getItems();
            addNameTextbox.value = '';
            addNStyleTextbox.value ='';
            addBrandTextbox.value ='';
        })
        .catch(error => console.error('Unable to add item.', error));
}

function deleteItem(id) {
    fetch(`${uri}/${id}`, {
        method: 'DELETE'
    })
        .then(() => getItems())
        .catch(error => console.error('Unable to delete item.', error));
}

function displayEditForm(id, version) {
    const item = beer.find(item => item.id === id);

    document.getElementById('edit-name').value = item.name;
    document.getElementById('edit-style').value = item.style;
    document.getElementById('edit-brand').value = item.brand;
    document.getElementById('edit-id').value = item.id;
    document.getElementById('edit-version').value = item.version;
    document.getElementById('editForm').style.display = 'block';
}

function updateItem() {
    const itemId = document.getElementById('edit-id').value;
    const version = document.getElementById('edit-version').value;
    const item = {
        id: parseInt(itemId, 10),
        complete: document.getElementById('edit-isComplete').checked,
        name: document.getElementById('edit-name').value.trim(),
        style: document.getElementById('edit-style').value.trim(),
        brand: document.getElementById('edit-brand').value.trim(),
        version: parseInt(version)
    };

    fetch(`${uri}/${itemId}`, {
        method: 'PUT',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(item)
    })
        .then(() => getItems())
        .catch(error => console.error('Unable to update item.', error));

    closeInput();

    return false;
}

function closeInput() {
    document.getElementById('editForm').style.display = 'none';
}

function _displayCount(itemCount) {
    const name = (itemCount === 1) ? 'Beer' : 'Beers';

    document.getElementById('counter').innerText = `${itemCount} ${name}`;
}

function _displayItems(data) {
    const tBody = document.getElementById('beer');
    tBody.innerHTML = '';

    _displayCount(data.length);

    const button = document.createElement('button');

    data.forEach(item => {
        let isCompleteCheckbox = document.createElement('input');
        isCompleteCheckbox.type = 'checkbox';
        isCompleteCheckbox.disabled = true;
        isCompleteCheckbox.checked = item.complete;

        let editButton = button.cloneNode(false);
        editButton.innerText = 'Edit';
        editButton.setAttribute('onclick', `displayEditForm(${item.id})`);

        let deleteButton = button.cloneNode(false);
        deleteButton.innerText = 'Delete';
        deleteButton.setAttribute('onclick', `deleteItem(${item.id})`);

        let tr = tBody.insertRow();


        let td2 = tr.insertCell(0);
        let textNode = document.createTextNode(item.name);
        td2.appendChild(textNode);

        let td3 = tr.insertCell(1);
        let textNod = document.createTextNode(item.style);
        td3.appendChild(textNod);

        let td4 = tr.insertCell(2);
        let textNoe = document.createTextNode(item.brand);
        td4.appendChild(textNoe);

        let td5 = tr.insertCell(3);
        td5.appendChild(editButton);

        let td6 = tr.insertCell(4);
        td6.appendChild(deleteButton);
    });

    beer = data;
}
