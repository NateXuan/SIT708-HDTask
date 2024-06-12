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
