import pandas as pd
import matplotlib.pyplot as plt
import sys

def generate_graph(data):
    # Create a DataFrame from the passed data
    df = pd.DataFrame(data)

    # Generate a plot
    plt.figure(figsize=(10, 6))
    plt.plot(df['X'], df['Y'], label='Line Plot', color='blue')
    plt.title('Sample Line Plot')
    plt.xlabel('X')
    plt.ylabel('Y')
    plt.legend()
    
    # Save the plot as an image
    plt.savefig("graph.png")
    plt.close()

if __name__ == "__main__":
    # Example: data could be passed as a JSON string or any other format
    data = {'X': [1, 2, 3, 4, 5], 'Y': [2, 3, 5, 7, 11]}
    generate_graph(data)
