-- Clear existing data (optional - uncomment if you want to start fresh)
-- DELETE FROM quote;
-- DELETE FROM group_membership;
-- DELETE FROM `group`;
-- DELETE FROM app_user;

-- Insert Users
INSERT INTO app_user (id, name) VALUES
                                    ('550e8400-e29b-41d4-a716-446655440000', 'john_doe'),
                                    ('550e8400-e29b-41d4-a716-446655440001', 'jane_smith'),
                                    ('550e8400-e29b-41d4-a716-446655440002', 'bob_wilson'),
                                    ('550e8400-e29b-41d4-a716-446655440003', 'alice_johnson'),
                                    ('550e8400-e29b-41d4-a716-446655440004', 'charlie_brown'),
                                    ('550e8400-e29b-41d4-a716-446655440005', 'diana_prince'),
                                    ('550e8400-e29b-41d4-a716-446655440006', 'edward_norton'),
                                    ('550e8400-e29b-41d4-a716-446655440007', 'fiona_apple'),
                                    ('550e8400-e29b-41d4-a716-446655440008', 'george_washington'),
                                    ('550e8400-e29b-41d4-a716-446655440009', 'helen_troy'),
                                    ('550e8400-e29b-41d4-a716-446655440010', 'isaac_newton'),
                                    ('550e8400-e29b-41d4-a716-446655440011', 'julia_roberts'),
                                    ('550e8400-e29b-41d4-a716-446655440012', 'kevin_bacon'),
                                    ('550e8400-e29b-41d4-a716-446655440013', 'laura_palmer'),
                                    ('550e8400-e29b-41d4-a716-446655440014', 'michael_scott');

-- Insert Groups
INSERT INTO `group` (id, name, created_by) VALUES
                                               (1, 'Philosophy Lovers', '550e8400-e29b-41d4-a716-446655440000'),
                                               (2, 'Science Enthusiasts', '550e8400-e29b-41d4-a716-446655440001'),
                                               (3, 'Literature Club', '550e8400-e29b-41d4-a716-446655440002'),
                                               (4, 'Movie Quotes', '550e8400-e29b-41d4-a716-446655440003'),
                                               (5, 'Inspirational Quotes', '550e8400-e29b-41d4-a716-446655440004'),
                                               (6, 'History Buffs', '550e8400-e29b-41d4-a716-446655440005'),
                                               (7, 'Sports Wisdom', '550e8400-e29b-41d4-a716-446655440006'),
                                               (8, 'Tech Gurus', '550e8400-e29b-41d4-a716-446655440007');

-- Insert Group Memberships
INSERT INTO group_membership (user_id, group_id, queue_number) VALUES
-- Philosophy Lovers members
('550e8400-e29b-41d4-a716-446655440000', 1, 1),
('550e8400-e29b-41d4-a716-446655440002', 1, 2),
('550e8400-e29b-41d4-a716-446655440005', 1, 3),
('550e8400-e29b-41d4-a716-446655440008', 1, 4),
('550e8400-e29b-41d4-a716-446655440010', 1, 5),

-- Science Enthusiasts members
('550e8400-e29b-41d4-a716-446655440001', 2, 1),
('550e8400-e29b-41d4-a716-446655440007', 2, 2),
('550e8400-e29b-41d4-a716-446655440010', 2, 3),
('550e8400-e29b-41d4-a716-446655440012', 2, 4),

-- Literature Club members
('550e8400-e29b-41d4-a716-446655440002', 3, 1),
('550e8400-e29b-41d4-a716-446655440003', 3, 2),
('550e8400-e29b-41d4-a716-446655440006', 3, 3),
('550e8400-e29b-41d4-a716-446655440009', 3, 4),
('550e8400-e29b-41d4-a716-446655440011', 3, 5),

-- Movie Quotes members
('550e8400-e29b-41d4-a716-446655440003', 4, 1),
('550e8400-e29b-41d4-a716-446655440011', 4, 2),
('550e8400-e29b-41d4-a716-446655440012', 4, 3),
('550e8400-e29b-41d4-a716-446655440014', 4, 4),

-- Inspirational Quotes members
('550e8400-e29b-41d4-a716-446655440004', 5, 1),
('550e8400-e29b-41d4-a716-446655440000', 5, 2),
('550e8400-e29b-41d4-a716-446655440005', 5, 3),
('550e8400-e29b-41d4-a716-446655440013', 5, 4);

-- Insert Quotes
INSERT INTO quote (quote, author, user_id, group_id) VALUES
-- Philosophy quotes
('I think therefore I am', 'René Descartes', '550e8400-e29b-41d4-a716-446655440000', 1),
('The unexamined life is not worth living', 'Socrates', '550e8400-e29b-41d4-a716-446655440002', 1),
('Whereof one cannot speak, thereof one must be silent', 'Ludwig Wittgenstein', '550e8400-e29b-41d4-a716-446655440005', 1),
('God is dead', 'Friedrich Nietzsche', '550e8400-e29b-41d4-a716-446655440008', 1),
('The only thing I know is that I know nothing', 'Socrates', '550e8400-e29b-41d4-a716-446655440010', 1),

-- Science quotes
('Imagination is more important than knowledge', 'Albert Einstein', '550e8400-e29b-41d4-a716-446655440001', 2),
('Science is a way of thinking much more than it is a body of knowledge', 'Carl Sagan', '550e8400-e29b-41d4-a716-446655440007', 2),
('The good thing about science is that it''s true whether or not you believe in it', 'Neil deGrasse Tyson', '550e8400-e29b-41d4-a716-446655440010', 2),
('If I have seen further it is by standing on the shoulders of Giants', 'Isaac Newton', '550e8400-e29b-41d4-a716-446655440012', 2),

-- Literature quotes
('It is a truth universally acknowledged, that a single man in possession of a good fortune, must be in want of a wife', 'Jane Austen', '550e8400-e29b-41d4-a716-446655440002', 3),
('All happy families are alike; each unhappy family is unhappy in its own way', 'Leo Tolstoy', '550e8400-e29b-41d4-a716-446655440003', 3),
('It was the best of times, it was the worst of times', 'Charles Dickens', '550e8400-e29b-41d4-a716-446655440006', 3),
('To be, or not to be, that is the question', 'William Shakespeare', '550e8400-e29b-41d4-a716-446655440009', 3),

-- Movie quotes
('May the Force be with you', 'Star Wars', '550e8400-e29b-41d4-a716-446655440003', 4),
('Here''s looking at you, kid', 'Casablanca', '550e8400-e29b-41d4-a716-446655440011', 4),
('You talking to me?', 'Taxi Driver', '550e8400-e29b-41d4-a716-446655440012', 4),
('I''ll be back', 'The Terminator', '550e8400-e29b-41d4-a716-446655440014', 4),

-- Inspirational quotes
('Be the change you wish to see in the world', 'Mahatma Gandhi', '550e8400-e29b-41d4-a716-446655440004', 5),
('The only way to do great work is to love what you do', 'Steve Jobs', '550e8400-e29b-41d4-a716-446655440000', 5),
('Success is not final, failure is not fatal: it is the courage to continue that counts', 'Winston Churchill', '550e8400-e29b-41d4-a716-446655440005', 5),
('Believe you can and you''re halfway there', 'Theodore Roosevelt', '550e8400-e29b-41d4-a716-446655440013', 5);