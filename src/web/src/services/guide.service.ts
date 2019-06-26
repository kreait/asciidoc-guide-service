
import axios from 'axios';

export default {

    get() {
    return axios.get('http://localhost:8081/test')
            .then((response) => response.data);
    },
};
