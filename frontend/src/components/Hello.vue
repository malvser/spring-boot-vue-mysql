<template>
  <div id="app">
  <b-container fluid>
    <!-- User Interface controls -->
    <b-row>
      <b-col md="6" class="my-1">
        <b-form-group horizontal label="Фильтр" class="mb-0">
          <b-input-group>
            <b-form-input v-model="filter" placeholder="Поиск" />
            <b-input-group-append>
              <b-btn :disabled="!filter" @click="filter = ''">Очистить</b-btn>
            </b-input-group-append>
          </b-input-group>
        </b-form-group>
      </b-col>
      <b-col md="6" class="my-1">
        <b-form-group horizontal label="Сортировка" class="mb-0">
          <b-input-group>
            <b-form-select v-model="sortBy" :options="sortOptions">
              <option slot="first" :value="null">-- пусто --</option>
            </b-form-select>
            <b-form-select :disabled="!sortBy" v-model="sortDesc" slot="append">
              <option :value="false">Asc</option>
              <option :value="true">Desc</option>
            </b-form-select>
          </b-input-group>
        </b-form-group>
      </b-col>

      <b-col md="6" class="my-1">
        <b-form-group horizontal label="К-во страниц" class="mb-0">
          <b-form-select :options="pageOptions" v-model="perPage" />
        </b-form-group>
      </b-col>
    </b-row>

    <!-- Main table element -->
    <b-table show-empty
             stacked="md"
             :items="items"
             :fields="fields"
             :current-page="currentPage"
             :per-page="perPage"
             :filter="filter"
             :sort-by.sync="sortBy"
             :sort-desc.sync="sortDesc"
             :sort-direction="sortDirection"
             @filtered="onFiltered"
    >

      <template slot="actions" slot-scope="row">
        <!-- We use @click.stop here to prevent a 'row-clicked' event from also happening -->
        <b-button size="sm" @click.stop="info(row.item)" class="mr-1">
          Редактировать
        </b-button>

      </template>

    </b-table>

    <b-row>
      <b-col md="6" class="my-1">
        <b-pagination :total-rows="totalRows" :per-page="perPage" v-model="currentPage" class="my-0" />
      </b-col>
    </b-row>


    <b-modal id="modalInfo" @hide="resetModal" :title="modalInfo.title" ok-only>

      <div class="label">Имя</div>
      <pre> <input type="text" v-model="user.firstName" ></pre>

        <div class="label">Фамилия</div>
        <pre><input v-model="user.lastName"></pre>

      <b-button size="sm" @click.stop="updateUser(user)" class="mr-1">
        Обновить
      </b-button>
      <b-button size="sm" type="danger" @click=" $root.$emit('bv::hide::modal', 'modalInfo')" class="mr-1">
        Отмена
      </b-button>

    </b-modal>


   <!-- <db-modal v-if="showModal"  :form="form" v-on:canclemodal="dialogVisible"></db-modal>-->
    <db-modal :users = "user"
      v-show="isModalVisible"
      @close="closeModal"
    />


  </b-container>
  </div>
</template>

<script>
  /* eslint-disable standard/object-curly-even-spacing */

  import {AXIOS} from './http-common'
  import DbModal from './DbModal.vue'
  export default {
    data () {
      return {
        isModalVisible: false,
        items: [],
        errors: [],
        user: {
          lastName: '',
          firstName: '',
          id: 0
        },
        fields: [
          { key: 'firstName', label: 'Имя', sortable: true, sortDirection: 'desc' },
          { key: 'lastName', label: 'Фамилия', sortable: true, 'class': 'text-center' },
          { key: 'actions', label: 'Действие' }
        ],
        currentPage: 1,
        perPage: 5,
        totalRows: 2,
        pageOptions: [ 5, 10, 15 ],
        sortBy: null,
        sortDesc: false,
        sortDirection: 'asc',
        filter: null,
        dialogFormVisible: false,
        modalInfo: { title: '', content: {firstName: '', lastName: ''} },
        form: '',
        showModal: true
      }
    },
    components: {
      DbModal
    },
    created () {
      console.log(' created called. ')
      AXIOS.get(`/users`)
        .then(response => {
          Object.keys(response.data).forEach(key => {
            this.items.push(response.data[key])
          })
          console.log(response.data)
        })
        .catch(e => {
          this.errors.push(e)
        })
    },
    computed: {
      sortOptions () {
        // Create an options list from our fields
        return this.fields
          .filter(f => f.sortable)
          .map(f => { return { text: f.label, value: f.key } })
      }
    },
    methods: {
      closeModal () {
        this.isModalVisible = false
      },
      dialogVisible: function () {
        this.dialogFormVisible = false
      },
      info (item) {
        this.modalInfo.title = `Редактирование`
        this.user.firstName = item.firstName
        this.user.lastName = item.lastName
        this.user.id = item.id
        console.log('user.firstName - ' + this.user.firstName)
        console.log('button -start')
        this.$root.$emit('bv::show::modal', 'modalInfo')
        console.log('button - end')
      },
      editModal (item) {
        this.isModalVisible = true
        this.user.firstName = item.firstName
        this.user.lastName = item.lastName
        this.user.id = item.id
        console.log('user.firstName - ' + this.user.firstName)
        console.log('button -start')
        console.log('button - end')
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
            this.$root.$emit('bv::hide::modal', 'modalInfo')
          })
          .catch(e => {
            this.errors.push(e)
          })
        location.reload()
      },
      resetModal () {
        this.user.firstName = ''
        this.user.lastName = ''
        this.user.id = ''
      },
      onFiltered (filteredItems) {
        // Trigger pagination to update the number of buttons/pages due to filtering
        this.totalRows = filteredItems.length
        this.currentPage = 1
      }
    }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
