<template>
  <b-container fluid>
    <!-- User Interface controls -->
    <b-row>
      <b-col md="6" class="my-1">
        <b-form-group horizontal label="Filter" class="mb-0">
          <b-input-group>
            <b-form-input v-model="filter" placeholder="Type to Search" />
            <b-input-group-append>
              <b-btn :disabled="!filter" @click="filter = ''">Clear</b-btn>
            </b-input-group-append>
          </b-input-group>
        </b-form-group>
      </b-col>
      <b-col md="6" class="my-1">
        <b-form-group horizontal label="Sort" class="mb-0">
          <b-input-group>
            <b-form-select v-model="sortBy" :options="sortOptions">
              <option slot="first" :value="null">-- none --</option>
            </b-form-select>
            <b-form-select :disabled="!sortBy" v-model="sortDesc" slot="append">
              <option :value="false">Asc</option>
              <option :value="true">Desc</option>
            </b-form-select>
          </b-input-group>
        </b-form-group>
      </b-col>
      <b-col md="6" class="my-1">
        <b-form-group horizontal label="Sort direction" class="mb-0">
          <b-input-group>
            <b-form-select v-model="sortDirection" slot="append">
              <option value="asc">Asc</option>
              <option value="desc">Desc</option>
              <option value="last">Last</option>
            </b-form-select>
          </b-input-group>
        </b-form-group>
      </b-col>
      <b-col md="6" class="my-1">
        <b-form-group horizontal label="Per page" class="mb-0">
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
        <b-button size="sm" @click.stop="info(row.item, row.index, $event.target)" class="mr-1">
          Редактировать
        </b-button>

      </template>

    </b-table>

    <b-row>
      <b-col md="6" class="my-1">
        <b-pagination :total-rows="totalRows" :per-page="perPage" v-model="currentPage" class="my-0" />
      </b-col>
    </b-row>

    <!-- Info modal -->
    <b-modal id="modalInfo" @hide="resetModal" :title="modalInfo.title" ok-only>
        <input type="hidden" v-model="user.id" >
      <div class="label">First Name</div>
      <pre> <input type="text" v-model="user.firstName" ></pre>

        <div class="label">Last Name</div>
        <pre><input v-model="user.lastName"></pre>

      <b-button size="sm" @click.stop="updateUser(user)" class="mr-1">
        Обновить
      </b-button>
      <b-button size="sm" type="danger" @click=" $root.$emit('bv::hide::modal', 'modalInfo')" class="mr-1">
        Отмена
      </b-button>

    </b-modal>


  </b-container>
</template>

<script>
  /* eslint-disable standard/object-curly-even-spacing */

  import {AXIOS} from './http-common'

  const items = [
    { isActive: true, age: 40, name: { first: 'Dickerson', last: 'Macdonald' } },
    { isActive: false, age: 21, name: { first: 'Larsen', last: 'Shaw' } },
    {
      isActive: false,
      age: 9,
      name: { first: 'Mini', last: 'Navarro' },
      _rowVariant: 'success'
    },
    { isActive: false, age: 89, name: { first: 'Geneva', last: 'Wilson' } },
    { isActive: true, age: 38, name: { first: 'Jami', last: 'Carney' } },
    { isActive: false, age: 27, name: { first: 'Essie', last: 'Dunlap' } },
    { isActive: true, age: 40, name: { first: 'Thor', last: 'Macdonald' } },
    {
      isActive: true,
      age: 87,
      name: { first: 'Larsen', last: 'Shaw' },
      _cellVariants: { age: 'danger', isActive: 'warning' }
    },
    { isActive: false, age: 26, name: { first: 'Mitzi', last: 'Navarro' } },
    { isActive: false, age: 22, name: { first: 'Genevieve', last: 'Wilson' } },
    { isActive: true, age: 38, name: { first: 'John', last: 'Carney' } },
    { isActive: false, age: 29, name: { first: 'Dick', last: 'Dunlap' } }
  ]

  export default {
    data () {
      return {
        items: [],
        errors: [],
        user: {
          lastName: '',
          firstName: '',
          id: 0
        },
        fields: [
          { key: 'firstName', label: 'First Name', sortable: true, sortDirection: 'desc' },
          { key: 'lastName', label: 'Last Name', sortable: true, 'class': 'text-center' },
          { key: 'actions', label: 'Actions' }
        ],
        currentPage: 1,
        perPage: 5,
        totalRows: items.length,
        pageOptions: [ 5, 10, 15 ],
        sortBy: null,
        sortDesc: false,
        sortDirection: 'asc',
        filter: null,
        dialogFormVisible: true,
        modalInfo: { title: '', content: {firstName: '', lastName: ''} },
        retrievedUser: {}

      }
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
      dialogVisible: function () {
        this.dialogFormVisible = false
      },
      info (item, index, button) {
        this.modalInfo.title = `Row index: ${index}`
        this.user.firstName = item.firstName
        this.user.lastName = item.lastName
        this.user.id = item.id
        console.log('user.firstName - ' + this.user.firstName)
        this.modalInfo.content = JSON.stringify(item, null, 2)
        console.log('button -start')
        this.$root.$emit('bv::show::modal', 'modalInfo')
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
            this.row = response.data
            console.log('row - update = ' + this.row)
            this.$root.$emit('bv::hide::modal', 'modalInfo')
          })
          .catch(e => {
            this.errors.push(e)
          })
      },
      resetModal () {
        this.user.firstName = ''
        this.user.lastName = ''
      },
      onFiltered (filteredItems) {
        // Trigger pagination to update the number of buttons/pages due to filtering
        this.totalRows = filteredItems.length
        this.currentPage = 1
      },
      edit: function (item, index) {
        console.log('start edit')
        this.dialogFormVisible = true
        AXIOS.get(`/user/` + item.id)
          .then(response => {
            this.form = response.data
            console.log(response.data)
          })
          .catch(e => {
            this.errors.push(e)
          })
      }
    }

}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>

</style>
