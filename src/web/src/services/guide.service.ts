
import axios from 'axios';

export default {

    get() {
    return axios.get('http://localhost:8081/guides/BenDenger/TestAsciDoc')
            .then((response) => response.data);
    },
    getGuide(id:String) {
        return axios.get('http://localhost:8081/guides/BenDenger/TestAsciDoc/'+id)
                .then((response) => response.data);
        }
};
