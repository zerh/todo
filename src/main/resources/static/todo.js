document.addEventListener('alpine:init', () => {
    const dataTheme = localStorage.getItem("data-theme");
    const html = document.documentElement;       
    html.setAttribute("data-theme",dataTheme);

    Alpine.data('app', ()=>({
        modal: false,
        login: true,
        tableLabel: 'Sin Items',
        items: [],
        selectedPage: 1,
        totalPages: 0,
        upsertItem: { 
            oldValue: '', 
            newValue: '', 
            done: 0, 
            description: '', 
            creationDate: new Date().toISOString().split("T")[0], 
            expirationDate: new Date().toISOString().split("T")[0], 
        },
        isEdit: false,
        upsertBtnText: 'Nuevo',
        username: '',
        token: '',
        indexToEdit: null,
        formData: {
            username: '',
            password: ''
        },
        init() {
            this.token = localStorage.getItem('jwt');
            this.username = localStorage.getItem('username');
            if(this.token !==null && this.token !== '') {
                this.login = false;
                this.loadTodos();
            } 
        },
        loadTodos(callback) {
            if(this.username) {
                fetch("/"+this.username+"/todo/"+this.selectedPage, {
                    method: "GET",
                    headers: {
                        "Content-Type": "application/json",
                        Accept: "application/json", 
                        Authorization: 'Bearer ' + this.token
                    }
                })
                .then(response => {
                    if(response.status==403){
                        this.login=true;
                        this.reset();
                        return null;
                    }
                    if(response.status=200){
                        return response.json()
                    } 
                    return null;
                    
                })
                .then(data=>{
                    if(data!==null){
                        this.totalPages = data.totalPages
                        this.items = data.list.map(e=>({
                            done: e.done==0? false: true,
                            textValue: e.textValue,
                            description: e.description,
                            creationDate: e.creationDate,
                            expirationDate: e.expirationDate
                        }));
                        if(callback) callback(data);
                    }
                    if(this.items.length>0) {
                        this.tableLabel = "Items";
                    } else {
                        this.tableLabel = "Sin Items";
                    }
                });
            }
        },

        selectPage(target) {
            this.selectedPage = parseInt(target.innerText);
            this.loadTodos();
            target.parentElement.querySelectorAll("a").forEach(el=>{
                el.style.textDecoration = 'none';
                el.style.fontWeight = "normal";
            });
            target.style.textDecoration = "underline";
            target.style.fontWeight = "bold";
        },
        toggleDone(index) {
            this.indexToEdit = index;
            this.upsertItem.done = this.items[index].done?1:0;
                this.upsertItem.oldValue = this.items[index].textValue;
                this.upsertItem.newValue = this.items[index].textValue;
                this.upsertItem.description = this.items[index].description;
                this.upsertItem.creationDate = this.items[index].creationDate;
                this.upsertItem.expirationDate = this.items[index].expirationDate;
            this.upsert();
            this.reset();
        },
        edit(index) {
            this.indexToEdit = index;
            const todoPanel = this.$refs.todoPanel;
            todoPanel.querySelectorAll("input[type='checkbox']").forEach(e=>e.disabled = true);
            todoPanel.querySelectorAll("a").forEach(e=>e.style.cursor = "not-allowed");
            Object.assign(this.upsertItem, this.items[index]);
            this.upsertItem.oldValue = this.items[index].textValue;
            this.upsertItem.newValue = this.items[index].textValue;

            this.isEdit = true;
            this.upsertBtnText = "Actualizar";
        },
        remove(index) {
            if(this.indexToEdit==null) {
                const textValue = this.items[index].textValue;
                fetch("/"+this.username+"/"+textValue, {
                    method: "DELETE",
                    headers: {
                        "Content-Type": "*/*",
                        Accept: "*/*",
                        Authorization: 'Bearer ' + this.token
                    }
                })
                .then(response => {
                    if(response.ok){
                        this.loadTodos();
                    }  
                });
            } else {
                modal = true;
            }
        },
        upsert() {
            if(this.indexToEdit){
                const index = this.indexToEdit;
                this.upsertItem.done = this.items[index].done?1:0;
                this.upsertItem.oldValue = this.items[index].textValue;
            }
            fetch("/"+this.username+"/todo", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Accept: "application/json",
                    Authorization: 'Bearer ' + this.token
                },
                body: JSON.stringify(this.upsertItem)
            })
            .then(response => {
                if(response.status == 403) {
                    this.reset();
                    this.login = true;
                }

                return response.json();
            })
            .then(item=>{
                
                if(item["textValue"]){
                    if(this.indexToEdit!==null) {
                        const selectedItem = this.items[this.indexToEdit];
                        Object.assign(selectedItem, item);
                        this.indexToEdit = null;
                    } else {
                        this.loadTodos((data)=>{
                            const effect = ()=> {
                                const el = document.getElementById("link_page_" + data.totalPages);
                                if(el && el.click){
                                    el.click(el);
                                    setTimeout(function() {
                                        const tds = document.querySelectorAll("#todoTable td details");
                                        const lastTd = tds[tds.length-1];
                                        if(lastTd){
                                            lastTd.classList.add("blink");
                                            setTimeout(function() {
                                                lastTd.classList.remove("blink");
                                            }, 1000);
                                        }
                                    }, 1000);
                                } else {
                                    setTimeout(effect, 500);
                                }
                            }
                            effect();
                            
                        });
                        
                    }
                    this.reset(); 
                }
            });
        },
        reset() {
            this.upsertItem = { 
                oldValue: '', 
                newValue: '', 
                done: 0, 
                description: '', 
                creationDate: new Date().toISOString().split("T")[0], 
                expirationDate: new Date().toISOString().split("T")[0]
            };
            this.upsertBtnText = "Nuevo";
            this.isEdit = false;
            this.$refs.todoPanel.querySelectorAll("input[type='checkbox']").forEach(e=>e.disabled = false);
            this.$refs.todoPanel.querySelectorAll("a").forEach(e=>e.style.cursor = "");

        },
        toggleTheme(e) {
            const html = document.documentElement;
            if(html.getAttribute("data-theme")==null || html.getAttribute("data-theme")=="dark"){
                html.setAttribute("data-theme","light");
                e.target.innerText = '🌙';
                localStorage.setItem("data-theme", "light");
            } else {
                html.setAttribute("data-theme","dark");
                e.target.innerText = '🌞';
                localStorage.setItem("data-theme", "dark");
            }
        },
        validateInput(e) {
            var regex = /^[a-zA-Z0-9\s]+$/;
            var valor = e.target.value;
            if (!regex.test(valor)) {
                e.target.value = valor.slice(0, -1);
            }
        }
    }));
});