<template>
  <transition name="modal-fade">
    <div class="modal-backdrop" >
      <div class="modal" id="modal"
           role="dialog"
           aria-labelledby="modalTitle"
           aria-describedby="modalDescription"
      >
        <header
          class="modal-header"
          id="modalTitle"
        >
          <slot name="header">
            Изменение пользователя

            <button
              type="button"
              class="btn-close"
              @click="close"
              aria-label="Close modal"
            >
              x
            </button>
          </slot>
        </header>
        <section
          class="modal-body"
          id="modalDescription"
          :model="users"
        >
          <slot name="body">
            <div class="label" >Имя</div>
            <input  type="text" v-model="users.firstName" >
              <pre></pre>
            <div class="label" >Фамилия</div>
            <input v-model="users.lastName">
          </slot>
        </section>
        <footer class="modal-footer">
          <slot name="footer">
            <button
              type="button"
              class="btn btn-success"
              @click="updateUser (users)"
              aria-label="Close modal"
            >
              Ok
            </button>

            <button
              type="button"
              class="btn btn-danger"
              @click="close"
              aria-label="Close modal"
            >
              Отмена
            </button>
          </slot>
        </footer>
      </div>
    </div>
  </transition>
</template>


<script>
  import {AXIOS} from './http-common'
  export default {
    name: 'modal',
    methods: {
      close () {
        this.$emit('close')
      },
      updateUser (item) {
        let firstName = item.firstName
        let lastName = item.lastName
        console.log('firstname - update = ' + firstName)
        console.log('user - id = ' + item.id)
        AXIOS.put(`/user/` + item.id, {
          firstName: firstName,
          lastName: lastName
        })
            .then(response => {
              this.user = response.data
              console.log('row - update = ' + this.row)
            })
            .catch(e => {
              this.errors.push(e)
            })
        location.reload()
      }
    },
    props: ['users']
  }

</script>
<style scoped>
  .modal-backdrop {
    position: fixed!important;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.3)!important;
    display: flex;
    justify-content: center!important;
    align-items: center!important;
  }

  .modal {
    background: #FFFFFF;
    box-shadow: 2px 2px 20px 1px!important;
    overflow-x: auto;
    display: flex;
    flex-direction: column!important;
    width: 30%;
    height: 35%;
    margin-left: 35%;
    margin-top: 10%;
  }

  .modal-header {
    padding: 6px;
    display: flex;
    margin-top: 1%;
  }
  .modal-footer {
    padding: 15px;
    display: flex;

  }

  .modal-header {
    border-bottom: 1px solid #eeeeee;
    color: #3f43ae;
    justify-content: space-between;
  }

  .modal-footer {
    border-top: 1px solid #eeeeee;
    justify-content: flex-end;
  }

  .modal-body {
    position: relative;
    padding: 20px 10px;
  }

  .btn-close {
    border: none;
    font-size: 20px;
    padding: 20px;
    cursor: pointer;
    font-weight: bold;
    color: #4AAE9B;
    background: transparent;
  }

  .btn-green {
    color: white;
    background: #4AAE9B;
    border: 1px solid #4AAE9B;
    border-radius: 2px;
  }
</style>
