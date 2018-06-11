<template>
  <div id="app">
  <b-container fluid>

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
        <b-form-group horizontal label="К-во элементов" class="mb-0">
          <b-form-select :options="pageOptions" v-model="perPage" />
        </b-form-group>
      </b-col>
    </b-row>


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

        <b-button size="sm" @click.stop="editModal(row.item)" class="mr-1">
          Редактировать
        </b-button>

      </template>

    </b-table>

    <b-row>
      <b-col md="6" class="my-1">
        <b-pagination :total-rows="totalRows" :per-page="perPage" v-model="currentPage" class="my-0" />
      </b-col>
    </b-row>

    <db-modal id="modal" :users = "user" @hide="resetModal"
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
        perPage: 10,
        totalRows: 2,
        pageOptions: [ 5, 10, 15 ],
        sortBy: null,
        sortDesc: false,
        filter: null
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
      editModal (item) {
        this.isModalVisible = true
        this.user.firstName = item.firstName
        this.user.lastName = item.lastName
        this.user.id = item.id
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


<style >

</style>
