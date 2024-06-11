const axios = require('axios');
const UserInformation = require('../models/userInformationModel');

exports.saveUserInformation = async (req, res) => {
    const { userId, age, gender, height, weight, healthCondition, goal, dietaryPreferences } = req.body;

    console.log('Received data:', req.body);

    try {
        const newUserInfo = new UserInformation({ userId, age, gender, height, weight, healthCondition, goal, dietaryPreferences });
        await newUserInfo.save();
        res.status(201).json({ message: 'User information saved successfully', userInfo: newUserInfo });
    } catch (error) {
        console.log('Error:', error);
        res.status(400).json({ message: 'Error saving user information', error });
    }
};

exports.updateUserInformation = async (req, res) => {
    const { userId, age, gender, height, weight, healthCondition, goal, dietaryPreferences } = req.body;

    console.log('Received data:', req.body);

    try {
        const userInfo = await UserInformation.findOne({ userId });
        if (!userInfo) {
            return res.status(404).json({ message: 'User information not found' });
        }

        userInfo.age = age;
        userInfo.gender = gender;
        userInfo.height = height;
        userInfo.weight = weight;
        userInfo.healthCondition = healthCondition;
        userInfo.goal = goal;
        userInfo.dietaryPreferences = dietaryPreferences;

        await userInfo.save();
        res.status(200).json({ message: 'User information updated successfully', userInfo });
    } catch (error) {
        console.log('Error:', error);
        res.status(500).json({ message: 'Error updating user information', error });
    }
};

exports.generateHealthPlan = async (req, res) => {
    const { userId } = req.body;

    try {
        const userInfo = await UserInformation.findOne({ userId });
        if (!userInfo) {
            return res.status(404).json({ message: 'User information not found' });
        }

        const userInformation = {
            age: userInfo.age,
            gender: userInfo.gender,
            height: userInfo.height,
            weight: userInfo.weight,
            health_condition: userInfo.healthCondition,
            goal: userInfo.goal,
            dietary_preferences: userInfo.dietaryPreferences
        };

        const axiosInstance = axios.create({
            baseURL: 'http://10.0.2.2:5000',
            timeout: 600000, 
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

        const response = await axiosInstance.post('/health_plan', userInformation, {
            retry: 3,
            retryDelay: 1000
        });

        return res.status(200).json({ healthPlan: response.data.healthPlan });
    } catch (error) {
        console.error('Error generating health plan:', error);
        return res.status(500).json({ message: 'Error generating health plan', error: error.toString() });
    }
};

exports.generateTodayPlan = async (req, res) => {
    const { userId } = req.body;

    try {
        const userInfo = await UserInformation.findOne({ userId });
        if (!userInfo) {
            return res.status(404).json({ message: 'User information not found' });
        }

        const userInformation = {
            age: userInfo.age,
            gender: userInfo.gender,
            height: userInfo.height,
            weight: userInfo.weight,
            health_condition: userInfo.healthCondition,
            goal: userInfo.goal,
            dietary_preferences: userInfo.dietaryPreferences
        };

        const axiosInstance = axios.create({
            baseURL: 'http://10.0.2.2:5000',
            timeout: 600000, 
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

        const response = await axiosInstance.post('/today_plan', userInformation, {
            retry: 3,
            retryDelay: 1000
        });

        return res.status(200).json({ todayPlan: response.data.todayPlan });
    } catch (error) {
        console.error('Error generating today\'s plan:', error);
        return res.status(500).json({ message: 'Error generating today\'s plan', error: error.toString() });
    }
};
