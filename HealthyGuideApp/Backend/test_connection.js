const axios = require('axios');

const userInformation = {
    age: 30,
    gender: 'male',
    height: 180,
    weight: 90,
    health_condition: 'good condition',
    goal: 'lose weight',
    dietary_preferences: 'low carb'
};

const axiosInstance = axios.create({
    baseURL: 'http://127.0.0.1:5000',
    timeout: 300000,
    headers: { 'Content-Type': 'application/json' }
});

axiosInstance.interceptors.response.use(null, async (error) => {
    const config = error.config;
    if (!config || !config.retry) return Promise.reject(error);

    config.__retryCount = config.__retryCount || 0;

    if (config.__retryCount >= config.retry) {
        return Promise.reject(error);
    }

    config.__retryCount += 1;

    const backoff = new Promise((resolve) => {
        setTimeout(() => {
            resolve();
        }, config.retryDelay || 1);
    });

    await backoff;
    return axiosInstance(config);
});

axiosInstance.post('/health_plan', userInformation, {
    retry: 3,
    retryDelay: 1000
})
.then(response => {
    console.log('Success:', response.data);
})
.catch(error => {
    console.error('Error:', error.response ? error.response.data : error.message);
});
