document.addEventListener('alpine:init', () => {
    Alpine.data('admin', function () {
        return {
            modal: false,
            users: [],
            upsertBtnText: "Nuevo",
            indexToEdit: null,
            token: '',
            username: '',
            upsertUser: {
                username: '',
                email: '',
                password: '',
                roles: {
                    admin: false,
                    user: false
                }
            },
            
            headers: {
                'Content-Type': 'application/json',
                Accept: 'application/json',
                Authorization: 'Bearer ' + localStorage.getItem('jwt')
            },

            closeModal() {
                this.modal = false;
                this.upsertUser = {
                    username: '',
                    email: '',
                    roles: {
                        admin: false,
                        user: false
                    }
                };
            },
            init() {
                fetch('/admin/users', { 
                    method: 'GET', 
                    headers:this.headers 
                }).then((response)=>{
                    if(response.status === 200) {
                        return response.json(); 
                    }
                }).then(data=>{
                    this.token = localStorage.getItem('jwt');
                    this.username = localStorage.getItem('username');
                    this.users = data;
                });
            },
            update() {
                fetch('/admin/users', { 
                    method:'POST', 
                    headers: this.headers, 
                    body: JSON.stringify( this.upsertUser )
                }).then(response => {
                    if(response.status == 200) {
                        return response.json();
                    }
                    if(response.status == 403) {
                        window.location.href = "/";
                    }
                }).then(data => {
                    const selectedItem = this.users[this.indexToEdit];
                    Object.assign(selectedItem, data);

                    this.selectedIndex = null;
                    this.closeModal();
                });
            },
            remove(index) {
                fetch('/admin/users/'+this.users[index].username, { 
                    method:'DELETE',
                    headers: this.headers
                }).then(response => {
                    if(response.status == 200) {
                        this.users.splice(index);
                    } else if(response.status == 404) {
                        alert("dame error");
                    } else if(response.status == 403) {
                       
                    }
                });
            }
        }
    });
})